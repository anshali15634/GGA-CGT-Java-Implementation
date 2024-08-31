package GGACGT;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Based on lower bound L2 calculation performed by Martello and Toth in
 * the following paper: Haouari, Mohamed & Gharbi, Anis. (2005).
 * Fast lifting procedures for the bin packing problem.
 * Discrete Optimization. 2. 201-218. 10.1016/j.disopt.2005.06.002. */

public class lowerBoundCalculator {
    public static int lowerBound(ArrayList<Integer> allItems){
        ArrayList<Integer> allItemsList = (ArrayList<Integer>) allItems.clone();
        Collections.sort(allItemsList, Comparator.reverseOrder()); // all items sorted in descending order
        ArrayList<Integer> L2 = new ArrayList<>();
        ArrayList<Integer> J1 = new ArrayList<>();
        ArrayList<Integer> J2 = new ArrayList<>();
        ArrayList<Integer> J3 = new ArrayList<>();
        int C = Bin.binFullCapacity;
        int Lmt;
        int J3sum=0, J2sum=0;
        for (int w = 0;(w <= C/2); w++){
            // find elements for J1, J2, J3 set
            for (int item : allItemsList ){
                if (C - w < item){
                    J1.add(item);
                }
                if (C/2 < item && item <= C - w){
                    J2.add(item);
                }
                if (w <= item && item <= C/2){
                    J3.add(item);
                }
            }
            for (int item: J3){
                J3sum+=item;
            }
            for (int item: J2){
                J2sum+=item;
            }
            int sum = (int) Math.ceil((J3sum-(J2.size()*C - J2sum))/(double)C);
            int max = (0>sum)?0:sum;
            Lmt = (int) (J1.size() + J2.size() + max);
            //System.out.printf("\nw = %d, L(%d) = %d",w, w, Lmt);
            //System.out.printf("\n%d + %d + max(0, ceil((%d - (%d*C - %d))/C)",J1.size(),J2.size(),J3sum,J2.size(), J2sum);
            L2.add(Lmt);
            J1.clear();
            J2.clear();
            J3.clear();
            J2sum = 0;
            J3sum = 0;
        }
        L2.sort(Comparator.reverseOrder()); // we want to get the largest number for lower bound
        return L2.getFirst();
    }
}
