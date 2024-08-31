package GGACGT;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class adaptiveMutationOperatorAndRP {

    // calculations required to calculate number of bins to eliminate during mutation:
    // nb= [t . e . pe]
    // e = (2 – (t / m)) / (t^(1/1.3))
    // pe = 1 – uniform(0, 1/ (t^(1/k))

    // if non-cloned solution, k = 1.3
    // if cloned solution. k = 4

    public static ArrayList<Bin> mutatedSolution(ArrayList<Bin> rawSol, boolean cloned){

        ArrayList <Bin> mutatedRawSol = new ArrayList<>(rawSol);
        ArrayList <Integer> remainingItems = new ArrayList<>();

        int nb = noOfBinsToEliminate(rawSol, cloned);

        // sort collection - least filled bin first
        Collections.sort(mutatedRawSol, Bin.remainingCapacityComparator);

        // note down all items from bins to be eliminated

        int binsCollected = 0;
        for (Bin bin : mutatedRawSol){
            remainingItems.addAll(bin.itemsInBin);
            binsCollected++;
            if (binsCollected == nb){
                break; // collected all items from bins to be eliminated
            }
        }

        // bins were sorted in ascending order so the least-filled bins are in the front
        mutatedRawSol.subList(0, nb).clear(); // remove bins to be eliminated

        ArrayList<Bin> finalMutatedRawSol = new ArrayList<>();

        // pass in remaining bins and leftover items to be reinserted using RP function
        finalMutatedRawSol = rearrangementByPairs(mutatedRawSol, remainingItems);

        return finalMutatedRawSol;
    }

    private static ArrayList<Bin> rearrangementByPairs(ArrayList<Bin> binCollection, ArrayList<Integer> remainingItems){

        Collections.shuffle(binCollection);
        Collections.shuffle(remainingItems);

        ArrayList <Bin> newBinCollection = new ArrayList<>();

        int j = 0;
        int p, i;
        int binItem1, binItem2, freeItem1, freeItem2;

        boolean breakToMainLoop = false;
        Bin bin;

        while (j<binCollection.size()){ // for every bin
            bin = binCollection.get(j);
            for (int s = 1; s < bin.itemsInBin.size(); s++){ // for every pair of items in bin
                p = s - 1;
                binItem1 = bin.itemsInBin.get(p); // (p,s) form a pair from binCollection
                binItem2 = bin.itemsInBin.get(s);
                for (int k = 1; k < remainingItems.size(); k++){
                    i = k - 1; // (i,k) form a pair from remainingItems
                    freeItem1 = remainingItems.get(i);
                    freeItem2 = remainingItems.get(k);
                    if (freeItem1>=binItem1+binItem2 && Bin.swapItemFeasible(bin, freeItem1, binItem1, binItem2)){
                        remainingItems.add(binItem1);
                        remainingItems.add(binItem2);
                        bin.itemsInBin.remove(Integer.valueOf(binItem1));
                        bin.itemsInBin.remove(Integer.valueOf(binItem2));
                        remainingItems.remove(Integer.valueOf(freeItem1));
                        bin.itemsInBin.add(freeItem1);
                        breakToMainLoop = true;
                        break;
                    }
                    if (freeItem2>=binItem1+binItem2 && Bin.swapItemFeasible(bin, freeItem2, binItem1, binItem2)){
                        remainingItems.add(binItem1);
                        remainingItems.add(binItem2);
                        bin.itemsInBin.remove(Integer.valueOf(binItem1));
                        bin.itemsInBin.remove(Integer.valueOf(binItem2));
                        remainingItems.remove(Integer.valueOf(freeItem2));
                        bin.itemsInBin.add(freeItem2);
                        breakToMainLoop = true;
                        break;
                    }
                    if (freeItem1+freeItem2>=binItem1+binItem2 && Bin.swapItemPairFeasible(bin, freeItem1, freeItem2, binItem1, binItem2)){
                        remainingItems.add(binItem1);
                        remainingItems.add(binItem2);
                        bin.itemsInBin.remove(Integer.valueOf(binItem1));
                        bin.itemsInBin.remove(Integer.valueOf(binItem2));
                        remainingItems.remove(Integer.valueOf(freeItem1));
                        remainingItems.remove(Integer.valueOf(freeItem2));
                        bin.itemsInBin.add(freeItem1);
                        bin.itemsInBin.add(freeItem2);
                        breakToMainLoop = true;
                        break;
                    }
                }
                if (breakToMainLoop){
                    breakToMainLoop = false;
                    break;
                }
            }
            newBinCollection.add(bin);
            j++;
        }

        // apply FF heuristic on the remaining items
        if (!remainingItems.isEmpty()){
            newBinCollection = FFnHeuristic.FF(newBinCollection, remainingItems);
        }

        return newBinCollection;
    }

    private static int noOfBinsToEliminate(List<Bin> rawSol, boolean cloned){

        // sort bin collection in descending order
        Collections.sort(rawSol, Bin.currentCapacityComparator);

        int t = 0;
        double k; // k defines rate of change of e and pe - parameter defined in paper.

        if (cloned){
            k = 1.3;
        }else{
            k = 4.0;
        }
        int m = rawSol.size(); // total number of bins
        // t = no. of incomplete bins
        for (Bin bin : rawSol){
            if (bin.currentCapacity != Bin.binFullCapacity) {
                t++;
            }
        }

        double e = ((2-((double) t /m))/(Math.pow(t, (1 / k))));
        double maxValue = 1 / Math.pow( t, (1 / k));

        // randomNum generated from a uniform distribution between 0 and 1/ (t^(1/k))
        double randomNum = Math.random() * maxValue;
        double pe = 1 - randomNum;

        int nb = (int) Math.ceil(t*e*pe);
        return nb;
    }
}
