package GGACGT;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 1.	Take nth element from G and R. These will be the parents for the crossover, p1 and p2.                                    <-
 * 2.	The crossover operation (x) will happen twice, to generate two children – p1 x p2, p2 x p1                                <-
 * 3.	Bins are to be in descending order of currentCapacity.                                                                    <-
 * 4.	For nth bin in G and R:                                                                                                   <-
     * a.	Compare currentCapacity of both bins.                                                                                 <-
     * b.	If Gc > Rc -> add G’s bin first, then R’s bin to childSolution.                                                       <-
     * c.	If Gc < Rc -> add R’s bin first, then G’s bin to childSolution.                                                       <-
     * d.	If Gc = Rc -> give priority to the top parent’s bin first, then bottom parent’s bin.                                  <-
 * 5.	After all pairs of bins are compared and added to newSolution, the longer solution’s bins are also added to newSolution.  <-
 * 6.	Bins with duplicate items are eliminated from the new solution, free items are reinserted with the FFD packing heuristic.
 * */

public class crossOverOperator {
    public static ArrayList<Integer> allItemsList;
    public static ArrayList<Bin> crossOver(ArrayList<Bin> parent1, ArrayList<Bin> parent2, int crossover_limit){
        ArrayList<Bin> childRawSolution = new ArrayList<>();
        for (int binIndex = 0; binIndex < crossover_limit; binIndex++) {
            if (parent1.get(binIndex).currentCapacity < parent2.get(binIndex).currentCapacity) {
                childRawSolution.add(new Bin(parent2.get(binIndex)));
                childRawSolution.add(new Bin(parent1.get(binIndex)));
            } else {// if current capacity equal, parent1 gets first priority.
                childRawSolution.add(new Bin(parent1.get(binIndex))); // if current capacity equal, parent1 gets first priority.
                childRawSolution.add(new Bin(parent2.get(binIndex)));
            }
        }

        if (parent1.size() > crossover_limit){
            for (int i = crossover_limit; i < parent1.size(); i++){
                childRawSolution.add(new Bin(parent1.get(i)));
            }
        }
        if (parent2.size() > crossover_limit){
            for (int i = crossover_limit; i < parent2.size(); i++){
                childRawSolution.add(new Bin(parent2.get(i)));
            }
        }

        // now we have both parents' bins in the child solution.
        // we need to get rid of the bins with duplicate items.

        ArrayList<Integer> copyItemList = new ArrayList<>();
        for (Integer item : allItemsList) {
            // Create a new Integer object with the same value and add it to the new list
            copyItemList.add(Integer.valueOf(item));
        }

        ArrayList<Integer> indexDeleteBins = new ArrayList<>();
        // for every bin
        for (int binIndex = 0; binIndex < childRawSolution.size(); binIndex++){
            // for every item in that bin
            for (int i = 0; i<childRawSolution.get(binIndex).itemsInBin.size();i++){ //(Integer item : childRawSolution.get(binIndex).itemsInBin){
                int index = Solution.findItem(copyItemList, childRawSolution.get(binIndex).itemsInBin.get(i));
                if (index == -1){ // the item was already in another bin - so delete this bin and reinsert the items which we marked as taken
                    for (int j = 0; j<i;j++){
                        copyItemList.add(childRawSolution.get(binIndex).itemsInBin.get(j));
                    }
                    indexDeleteBins.add(binIndex);
                    break;
                }else{ // set that item to -1, to say "already in a bin"
                    copyItemList.set(index, -1);
                }
            }
        }

        // deleting bins with duplicate items
        Collections.sort(indexDeleteBins, Collections.reverseOrder());
        for (int index : indexDeleteBins) {
            childRawSolution.remove(index);
        }

        // retrieve the remaining items
        ArrayList<Integer> remainingItems = new ArrayList<>();
        for (Integer item : copyItemList) {
            if (item != -1) {
                remainingItems.add(item);
            }
        }

        //ArrayList <Bin> finalChildRawSolution = new ArrayList<>());
        //finalChildRawSolution = firstFitDecreasing(childRawSolution, remainingItems);

        return new ArrayList<>(firstFitDecreasing(childRawSolution, remainingItems));
    }
    private static ArrayList<Bin> firstFitDecreasing(ArrayList<Bin> existingBinCollection, ArrayList <Integer> remainingItems){
        Collections.sort(remainingItems, Collections.reverseOrder());
        boolean binInputStatus = false;
        for (Integer next_Item : remainingItems) {
            binInputStatus = false;
            // first check if it fits in existing bins
            for (Bin bin : existingBinCollection) {
                binInputStatus = bin.addToBin(next_Item);
                if (binInputStatus) { // if fits into an existing bin, no need to look further - go to next item
                    break;
                }
            }
            if (!binInputStatus) { // if not fitting into existing bins, create a new bin
                Bin newBin = new Bin();
                newBin.addToBin(next_Item);
                existingBinCollection.add(newBin);
            }
        }
        return existingBinCollection;
    }
}
