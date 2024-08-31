package GGACGT;
import java.util.ArrayList;
import java.util.Comparator;

public class Bin {
    public static int binFullCapacity=0; // set to 10000 after wards
    public int currentCapacity;
    public ArrayList<Integer> itemsInBin;
    public Bin(){
        this.currentCapacity=0;
        this.itemsInBin = new ArrayList<>();
    }
    public Bin(Bin other) { // copy constructor
        this.currentCapacity = other.currentCapacity;
        // Create a new ArrayList and copy the elements from the other Bin's ArrayList
        this.itemsInBin = new ArrayList<>(other.itemsInBin);
    }
    public boolean addToBin(int item){
        int futureCapacity = this.currentCapacity + item;
        if (futureCapacity <= binFullCapacity) {
            this.itemsInBin.add(item);
            this.currentCapacity = futureCapacity;
            return true;
        }else{
            return false;
        }
    }
    public void printBinContents(){
        System.out.print(this.itemsInBin);
    }
    public static Comparator<Bin> remainingCapacityComparator = new Comparator<Bin>() {
        @Override
        public int compare(Bin bin1, Bin bin2) {
            int remainingCapacity1 = binFullCapacity - bin1.currentCapacity;
            int remainingCapacity2 = binFullCapacity - bin2.currentCapacity;

            // Compare remaining capacities
            return Integer.compare(remainingCapacity2, remainingCapacity1);
        }
    };
    public static Comparator<Bin> currentCapacityComparator = new Comparator<Bin>() {
        @Override
        public int compare(Bin bin1, Bin bin2) {
            // allows sorting of bins in descending order of current capacity
            return Integer.compare(bin2.currentCapacity, bin1.currentCapacity);
        }
    };
    public static boolean swapItemFeasible(Bin bin, Integer itemInsert, Integer itemRemove1, Integer itemRemove2){
        if (bin.currentCapacity - itemRemove1 - itemRemove2 + itemInsert <= Bin.binFullCapacity){
            return true;
        }else{
            return false;
        }
    }
    public static boolean swapItemPairFeasible(Bin bin, Integer itemInsert1, Integer itemInsert2, Integer itemRemove1, Integer itemRemove2){
        if (bin.currentCapacity - itemRemove1 - itemRemove2 + itemInsert1 + itemInsert2 <= Bin.binFullCapacity){
            return true;
        }else{
            return false;
        }
    }
}
