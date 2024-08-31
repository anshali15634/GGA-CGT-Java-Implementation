package GGACGT;
import java.util.ArrayList;
public class FFnHeuristic {
    public static ArrayList<Bin> FFn(ArrayList<Integer> itemsAbove50, ArrayList<Integer> permutationRemainingItems) {
        ArrayList<Bin> binCollection = new ArrayList<>();

        // if we have items above 50% capacity, give them separate bins and add to a collection of bins.
        // this will be the base for our solution.

        if (!itemsAbove50.isEmpty()){
            for (Integer item : itemsAbove50) {
                Bin bin = new Bin();
                bin.addToBin(item);
                binCollection.add(bin);
            }
        }
        // perform FF heuristic on remaining items
        ArrayList<Bin> updatedBinCollection = FF(binCollection, permutationRemainingItems);

        return updatedBinCollection;
    }

    public static ArrayList<Bin> FF(ArrayList<Bin> existingBinCollection, ArrayList<Integer> permutationRemainingItems) {
        boolean binInputStatus = false;
        for (Integer next_Item : permutationRemainingItems) {
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

