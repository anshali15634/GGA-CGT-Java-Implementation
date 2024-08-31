package GGACGT;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class problemInstance {
    public String problemInstanceName; // 'TEST0049'
    public int numberOfItemWeights; // number m of different item weights
    public int binCapacity; // capacity C of bins
    public int[] itemWeights; // array of all the item weights
    public int[] noOfItems; // number of items with that item weight

    public ArrayList<Integer> allItemsOrderedList = new ArrayList<>();

    public problemInstance(String pi, int niw, int bc){
        problemInstanceName = pi;
        numberOfItemWeights = niw;
        binCapacity = bc;
        itemWeights = new int[niw];
        noOfItems = new int[niw];
    }
    public void addItemWeight(int weight, int index){
        itemWeights[index]=weight;
    }
    public void addNumItemWeight(int numberOfWeights, int index){
        noOfItems[index]=numberOfWeights;
        for (int i = 0; i< numberOfWeights; i++){
            allItemsOrderedList.add(itemWeights[index]);
        }
    }
    public void printInfo() {
        System.out.println("Problem Instance Name: " + problemInstanceName);
        System.out.println("Number of Item Weights: " + numberOfItemWeights);
        System.out.println("Bin Capacity: " + binCapacity);
        System.out.println("Item Weights and Number of Items:");

        for (int i = 0; i < numberOfItemWeights; i++) {
            System.out.println("   Item Weight: " + itemWeights[i] + ", Number of Items: " + noOfItems[i]);
        }
        System.out.println("All Items Order: ");
        System.out.println(allItemsOrderedList);
    }




}
