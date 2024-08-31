package GGACGT;
import java.util.Comparator;

class SolutionComparator implements Comparator<Solution> {
    @Override
    public int compare(Solution s1, Solution s2) {
        // helps compare and sort solutions in descending order of fitness
        return Double.compare(s2.fitnessValue, s1.fitnessValue);
    }

}
