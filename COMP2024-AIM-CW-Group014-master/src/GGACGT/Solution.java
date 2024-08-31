package GGACGT;
import java.util.ArrayList;
import java.util.Comparator;

public class Solution {
    public static ArrayList<Integer> itemList;
    private static int numSols=0; // used to make a unique ID for each solution
    public boolean cloned;
    public final int solutionID; // needed to identify if solution already present in a set or not
    public int life_span;
    public ArrayList<Bin> rawSolution = new ArrayList<>();
    public String encodedSolution = ""; // itemPart + ":" + groupPart
    public String groupPart = "";
    public StringBuilder itemPart;
    public int numberOfBins;
    public double fitnessValue;
    public static final String codeChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    // this encoding can support solutions with at most 52 bins.
    public Solution(ArrayList<Bin> rawSol, boolean cloned){
        numSols+=1;
        this.solutionID = numSols;
        for (Bin bin : rawSol){
            this.rawSolution.add(new Bin(bin));
        }
        itemPart = new StringBuilder("v".repeat(itemList.size())); // placeholder letter v, replaced by encoding later
        groupPart = codeChars.substring(0,this.rawSolution.size());
        this.numberOfBins = groupPart.length();
        this.encodedSolution = encodeSolution(rawSol, itemList)+":"+groupPart;
        this.fitnessValue = this.fitnessFunction();
        this.life_span = 0;
        this.cloned = cloned;
    }

    public Solution(Solution sol) {
        // clones should also have unique ids
        numSols++;
        this.solutionID = numSols;
        // deep copy the raw solution
        for (Bin bin : sol.rawSolution){
            this.rawSolution.add(new Bin(bin));
        }
        itemPart = sol.itemPart;
        groupPart = sol.groupPart;
        this.numberOfBins = groupPart.length();
        this.encodedSolution = sol.encodedSolution;
        this.fitnessValue = sol.fitnessValue;
        this.life_span = 0;
        this.cloned = true;
    }

    // encodeSolution() converts the given raw solution into its encoded version
    private String encodeSolution(ArrayList<Bin> rawSol, ArrayList <Integer> itemList){
        ArrayList<Integer> copyItemList = new ArrayList<>();
        for (Integer item : itemList) {
            copyItemList.add(Integer.valueOf(item));
        }
        int countBin = -1;
        for (Bin bin : rawSol){
            countBin++;
            for (Integer binItem : bin.itemsInBin) {
                int index = findItem(copyItemList, binItem);
                itemPart.setCharAt(index, codeChars.charAt(countBin));
                copyItemList.set(index,-1);
            }
        }
        return itemPart.toString();
    }
    private double fitnessFunction(){
        double sum = 0;
        for (Bin bin : rawSolution){
            sum += Math.pow(((double)bin.currentCapacity / (double)Bin.binFullCapacity),2);
        }
        double fitness =  sum/(double)this.numberOfBins; // return rounded fitness value to 3 decimal places
        return fitness;
    }
    public static int findItem(ArrayList <Integer> itemList,int itemName){
        for (int i = 0; i < itemList.size(); i++) {
            if (itemList.get(i) == itemName) {
                return i; // Return the index if item found
            }
        }
        return -1; // Return -1 if item not found
    }
    public int printSolutionInfo(){
        System.out.printf("Solution ID: %d",this.solutionID);
        System.out.printf("\nRaw Solution: ");
        for (Bin bin : this.rawSolution) {
            bin.printBinContents();
        }
        System.out.printf("\nEncoded Solution: %s",this.encodedSolution);
        System.out.println("\nFitness Value: "+this.fitnessValue);
        System.out.println("Number Of Bins: "+this.numberOfBins);
        System.out.println("-----------------------------------------------------------");
        return this.numberOfBins;
    }
    public static Comparator<Solution> ascendingOrderFitness = new Comparator<Solution>() {
        @Override
        public int compare(Solution sol1, Solution sol2) {
            // allows sorting of bins in descending order of current capacity
            return Double.compare(sol1.fitnessValue, sol2.fitnessValue);
        }
    };

    public void incrementLifeSpan(){
        this.life_span+=1;
    }
}
