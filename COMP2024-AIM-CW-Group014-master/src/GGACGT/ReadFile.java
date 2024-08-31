package GGACGT;
import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.Scanner; // Import the Scanner class to read text files

// 'TEST0049'        -> test name
//      43           -> number m of different item weights
//   10000           -> capacity C of the bins
//    2472         1 -> item weight 1 # of items of weight 1
//    2371         2
//    2027         1

public class ReadFile {
    public static problemInstance[] readFile() {
        problemInstance[] problemArray = new problemInstance[5];
        try {
            int problemInstanceNumber = -1;
            File problemInstancesFile = new File("src/BPP.txt");
            Scanner myReader = new Scanner(problemInstancesFile);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                if (data.contains("TEST")) {
                    problemInstanceNumber += 1;
                    String testName = data;
                    int noOfWeights = Integer.parseInt(myReader.nextLine().trim());
                    int binCapacity = Integer.parseInt(myReader.nextLine().trim());
                    problemInstance probInst = new problemInstance(testName, noOfWeights, binCapacity);
                    problemArray[problemInstanceNumber] = probInst;
                    int count = 0;
                    while (count != noOfWeights) {
                        String weightData = myReader.nextLine();
                        String[] parts = weightData.trim().split("\\s+");
                        int itemWeight = Integer.parseInt(parts[0]); // item weight
                        int no_Of_Items = Integer.parseInt(parts[1]);// # of items of weight
                        probInst.addItemWeight(itemWeight, count);
                        probInst.addNumItemWeight(no_Of_Items, count);
                        count++;
                    }
                }
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("File containing problem instances (BPP.txt) not found.");
            e.printStackTrace();
        }
        return problemArray;
    }
}