package GGACGT;
import java.lang.reflect.Array;
import java.util.*;
import java.io.FileWriter;
import java.io.IOException;

/**Java Implementation of GGA-CGT (Grouping Genetic Algorithm with Controlled Gene Transmission) algorithm for solving the 1D BPP (Bin Packing Problem)
 * @author Anshali Manoharan */

public class Main {
    public static void main(String[] args) throws IOException {

//        String filePath = "outputRuns.txt";
//        FileWriter writer = new FileWriter(filePath);
//        ArrayList binRuns = new ArrayList();
        int probNum = 5; // can be either 0, 1, 2, 3 or 4

        ArrayList avgTime = new ArrayList<>();

        for (int instance = 0; instance < probNum; instance++) {
//            int sumTime = 0;
            //running the following 30 times
//            for (int run = 0; run < 30; run++) {

                int numOfSolutions = 100; // number of solutions for population
                int nm = 83; // number of individuals to be mutated
                int numberOfGenerations = 100; // number of generations for population to pass

                // reads from BPP.txt and stores each problem instance in a problemInstance object, and stores in an array
                problemInstance[] problemArray = ReadFile.readFile();
                // to store the population of solutions
                ArrayList<Solution> population = new ArrayList<>();
                // to choose which problem from BPP.txt to work on

                // items from problem instance with more than 50% of bin's capacity goes in this array
                ArrayList<Integer> itemsAboveFifty = new ArrayList<>();
                // remaining items will be filled in this array
                ArrayList<Integer> remainingItems = new ArrayList<>();

                // to store the solution with the highest fitness
                Solution best_Solution;

                // initialize bin capacity in Bin class for all upcoming bins
                Bin.binFullCapacity = problemArray[instance].binCapacity;

                // set a centralised order of all the items for the group encoding to refer to
                Solution.itemList = problemArray[instance].allItemsOrderedList;
                crossOverOperator.allItemsList = problemArray[instance].allItemsOrderedList;

                /** STEP 1. Process the items from problem instance
                 * separate the items to two separate arrays for implementing FFñ heuristic
                 * */

                // separating the items from problem instance into the two arrays - itemsAboveFifty and remainingItems
                // these two arrays are then used by FFñ heuristic to create initial solutions
                for (int i = 0; i < problemArray[instance].itemWeights.length; i++) {
                    if (problemArray[instance].itemWeights[i] > 0.5 * problemArray[instance].binCapacity) {
                        for (int k = 0; k < problemArray[instance].noOfItems[i]; k++) {
                            itemsAboveFifty.add(problemArray[instance].itemWeights[i]);
                        }
                    } else {
                        for (int k = 0; k < problemArray[instance].noOfItems[i]; k++) {
                            remainingItems.add(problemArray[instance].itemWeights[i]);
                        }
                    }
                }

                // display problem instance information
                System.out.println("-----------------------------------------------------------");
                problemArray[instance].printInfo();
                System.out.println("-----------------------------------------------------------");

                long startTime = System.nanoTime();
                // calculate lower bound for solution's number of bins
                int lowerBound = lowerBoundCalculator.lowerBound(problemArray[instance].allItemsOrderedList);

                System.out.printf("Estimated minimum number of bins required to pack all items: %d", lowerBound);
                System.out.println("\n-----------------------------------------------------------");

                /** STEP 2. Generate solutions using FFñ heuristic to create initial population */

                Random random = new Random();
                Solution solution;
                for (int i = 0; i < numOfSolutions; i++) {
                    // creates random permutation of remainingItems
                    Collections.shuffle(remainingItems);
                    // stores the solution in the form of an array of type Bin
                    ArrayList<Bin> rawSolution = FFnHeuristic.FFn(itemsAboveFifty, remainingItems);
                    // solution takes rawSolution and converts it into the encoded format (using group encoding)
                    solution = new Solution(rawSolution, false);
                    population.add(solution);
                }

                // these arrays are to be used for crossover, controlled replacement later

                // G stores 10 randomly selected individuals from the top 20 individuals of population P
                ArrayList<Solution> G;
                // R stores 10 randomly selected individuals from the population excluding top 10 individuals and G set
                ArrayList<Solution> R = new ArrayList<>();
                // B stores top 10 individuals of population
                ArrayList<Solution> eliteGroupB;
                // PwithoutB references the population without the top 10 individuals
                ArrayList<Solution> PwithoutB;
                // top20 stores the top 20 individuals of the population
                ArrayList<Solution> top20;
                // these arrays keep track of the solution IDs of solutions in G, R, B
                ArrayList<Integer> GSolutionIDs = new ArrayList<>();
                ArrayList<Integer> RSolutionIDs = new ArrayList<>();
                ArrayList<Integer> BSolutionIDs = new ArrayList<>();

                // to store clones of the top 10 individuals given their life_span < 10
                ArrayList<Solution> cloned_Elite_Individuals = new ArrayList<>();

                // arrays to store the children created from crossover - and their IDs
                ArrayList<Solution> childrenAfterCrossOver = new ArrayList<>();
                ArrayList<Integer> childIDs = new ArrayList<>();

                // population sorted in descending order of fitness
                population.sort(new SolutionComparator());
                // initially most fit solution is in the beginning
                best_Solution = population.getFirst();

                System.out.println("Best solution generated in initial population: ");
                best_Solution.printSolutionInfo();
                // for loop is where parent selection, crossover, replacement and cloning occurs
                for (int a = 0; (a < numberOfGenerations && best_Solution.numberOfBins > lowerBound); a++) {

                    /** STEP 3. Perform controlled selection of individuals for crossover - parent selection
                     * choose 20 individuals to be parents
                     * 10 random individuals should be from the top 20 individuals (store in G)
                     * remaining 10 should be randomly picked from rest of population (store in R) */

                    population.sort(new SolutionComparator()); // population sorted in descending order of fitness

                    // use subList to extract and sort into respective sets
                    List<Solution> sublist = population.subList(0, 20);
                    // top20 represents the top 20 fittest individuals of the population
                    top20 = new ArrayList<>(sublist);
                    sublist = population.subList(0, 10);
                    // eliteGroupB represents the top 10 fittest individuals of the population
                    eliteGroupB = new ArrayList<>(sublist);
                    sublist = population.subList(10, population.size());
                    // PwithoutB stores the portion of the population which does not contain the eliteGroupB
                    PwithoutB = new ArrayList<>(sublist);

                    // keep track of the solutions IDs which are a part of eliteGroupB
                    for (Solution sol : eliteGroupB) {
                        BSolutionIDs.add(sol.solutionID);
                    }

                    // group G will contain 10 random individuals from the top 20 individuals of the population
                    // and keep track of their IDs
                    Collections.shuffle(top20, random);
                    sublist = top20.subList(0, 10);

                    G = new ArrayList<>(sublist);

                    for (Solution sol : G) {
                        GSolutionIDs.add(sol.solutionID);
                    }

                    // from the portion of the population where we don't include the top 10 individuals
                    // we randomly choose 10 individuals to be in set R.
                    Collections.shuffle(PwithoutB, random);
                    int RCount = 0;
                    int randomIndex;
                    while (RCount != 10) {
                        randomIndex = random.nextInt(PwithoutB.size());
                        Solution randomSolution = PwithoutB.get(randomIndex);
                        // to make sure the randomly chosen solution doesn't already exist in G and R
                        if (!GSolutionIDs.contains(randomSolution.solutionID) && !RSolutionIDs.contains(randomSolution.solutionID)) {
                            R.add(randomSolution);
                            RSolutionIDs.add(randomSolution.solutionID);
                            RCount += 1;
                        }
                    }

                    /** STEP 4. Apply gene level crossover + FFD to the selected individuals */

                    // this arraylist will store the 20 children created from the crossover process
                    // we get 2 children from each pair of parents (G,R) - in total 20 children are the product of crossover.
                    ArrayList<Bin> parent1RawSol;
                    ArrayList<Bin> parent2RawSol;

                    for (int n = 0; n < 10; n++) {
                        // crossover happens between each pair of parents in G and R
                        Solution parent1 = G.get(n);
                        Solution parent2 = R.get(n);

                        // copy from each parent the rawSolutions
                        parent1RawSol = new ArrayList<>();
                        parent2RawSol = new ArrayList<>();

                        for (Bin bin : parent1.rawSolution) {
                            parent1RawSol.add(new Bin(bin));
                        }

                        for (Bin bin : parent2.rawSolution) {
                            parent2RawSol.add(new Bin(bin));
                        }
                        // before crossover sort each solution's bins so that most filled bin comes first
                        parent1RawSol.sort(Bin.currentCapacityComparator);
                        parent2RawSol.sort(Bin.currentCapacityComparator);

                        ArrayList<Bin> rawChildSolution1;
                        ArrayList<Bin> rawChildSolution2;

                        // defines the common number of bins in both solutions
                        int crossover_limit = Math.min(parent1.numberOfBins, parent2.numberOfBins);

                        // crossOverOperator performs crossover, removes duplicate items and applies FFD to get a valid child solution
                        rawChildSolution1 = crossOverOperator.crossOver(parent1RawSol, parent2RawSol, crossover_limit);
                        rawChildSolution2 = crossOverOperator.crossOver(parent2RawSol, parent1RawSol, crossover_limit);

                        Solution childSolution1 = new Solution(rawChildSolution1, false);
                        Solution childSolution2 = new Solution(rawChildSolution2, false);

                        // add children to array
                        childrenAfterCrossOver.add(childSolution1);
                        childrenAfterCrossOver.add(childSolution2);

                        childIDs.add(childSolution1.solutionID);
                        childIDs.add(childSolution2.solutionID);

                        rawChildSolution1.clear();
                        rawChildSolution2.clear();

                        parent1RawSol.clear();
                        parent2RawSol.clear();
                    }

                    /** STEP 5. Perform controlled replacement - replacing selected individuals with the 20 children into the population */
                    /** 1. the first 10 children replace the set R in the population
                     *  2. the remaining 10 children replace any solutions in P \ (B U R) with duplicate fitness values.
                     *     if we run out of solutions with duplicated fitness values - we replace the worst solutions.
                     * */

                    // replace set R with first 10 children
                    int childCount = 0;
                    for (int i = 0; i < population.size(); i++) {
                        if (RSolutionIDs.contains(population.get(i).solutionID)) {
                            population.set(i, childrenAfterCrossOver.get(childCount)); // if solution in set R, replace with new child
                            childCount++;
                        }
                        if (childCount == 10) { // if we already inserted first 10 children, break out of loop
                            break;
                        }
                    }

                    // update set R as the first 10 children solution IDs
                    for (int i = 0; i < 10; i++) {
                        RSolutionIDs.set(i, childrenAfterCrossOver.get(i).solutionID);
                    }

                    // now we look at how to insert the remaining 10 children into the population
                    // first we find the solutions with duplicated fitness values

                    ArrayList<Integer> duplicateSolutionIndexes = new ArrayList<>();

                    population.sort(new SolutionComparator()); // sort population in descending order of fitness

                    // we keep track of previous solution to check for duplicate fitness
                    Solution prevSolution = population.getFirst();
                    for (int k = 1; k < population.size(); k++) {
                        Solution thisSolution = population.get(k);
                        // store index of solutions with duplicated fitness
                        // remaining 10 children should replace anyone in population but should not be in set B or R.
                        if (prevSolution.fitnessValue == thisSolution.fitnessValue && !RSolutionIDs.contains(thisSolution.solutionID) && !BSolutionIDs.contains(thisSolution.solutionID)) {
                            duplicateSolutionIndexes.add(k);
                        }
                        if (duplicateSolutionIndexes.size() == 10) {
                            break; // we have enough of solutions to replace - no need to look further
                        }
                        prevSolution = thisSolution;
                    }

                    // we already inserted children 0 to 9, now we insert children 10 to 19.
                    int leftOverChildIndex = 10;

                    // for all found duplicate indexes (<=10 indexes), replace with that number of children
                    for (int index : duplicateSolutionIndexes) {
                        population.set(index, childrenAfterCrossOver.get(leftOverChildIndex));
                        leftOverChildIndex++;
                    }

                    if (leftOverChildIndex != 20) { // still some children remain to be inserted into population
                        // first sort population in ascending order of fitness to get the worst solutions in beginning
                        Collections.sort(population, Solution.ascendingOrderFitness);
                        for (int j = 0; (leftOverChildIndex != 20); j++) {
                            // set the worst solution to leftover children
                            population.set(j, childrenAfterCrossOver.get(leftOverChildIndex));
                            leftOverChildIndex++;
                        }
                    }

                    /** STEP 6. Select the top nm individuals and clone elite individuals if their life span < 10 */

                    for (Solution sol : eliteGroupB) {
                        if (sol.life_span < 10) {
                            Solution cloned_sol = new Solution(sol); // clones the solution and flags the solution as cloned
                            cloned_Elite_Individuals.add(cloned_sol);
                        }
                    }

                    /** STEP 7. Apply adaptive mutation + RP (rearrangement by pairs) on the nm individuals*/
                    population.sort(new SolutionComparator()); // descending order of fitness

                    Solution mutatedSol;
                    ArrayList<Bin> rawSolToBeMutated = new ArrayList<>();
                    for (int i = 0; i < nm; i++) {

                        for (Bin bin : population.get(i).rawSolution) {
                            rawSolToBeMutated.add(new Bin(bin));
                        }

                        ArrayList<Bin> mutatedRawSol = new ArrayList<>(adaptiveMutationOperatorAndRP.mutatedSolution(rawSolToBeMutated, population.get(i).cloned));
                        mutatedSol = new Solution(mutatedRawSol, false);
                        population.set(i, mutatedSol);
                        rawSolToBeMutated.clear();
                    }

                    /** STEP 8. Apply controlled replacement to introduce the clones
                     every clone can be entered into the population in two ways:
                     (a) if there are solutions with duplicated fitness, then the clone will replace one of these
                     (b) if the first alternative is not possible - then clone replaces worst solution.*/

                    duplicateSolutionIndexes.clear();
                    prevSolution = population.getFirst(); // we keep track of previous solution to check for duplicate fitness
                    for (int k = 1; k < population.size(); k++) {
                        Solution thisSolution = population.get(k);
                        // store index of solutions with duplicated fitness
                        if (prevSolution.fitnessValue == thisSolution.fitnessValue) {
                            duplicateSolutionIndexes.add(k);
                        }
                        if (duplicateSolutionIndexes.size() == cloned_Elite_Individuals.size()) {
                            break; // we have enough of duplicate solutions to replace
                        }
                        prevSolution = thisSolution;
                    }

                    // insert all cloned individuals into population - replace duplicate solutions
                    int clonedIndvIndex = cloned_Elite_Individuals.size() - 1;
                    for (int index : duplicateSolutionIndexes) {
                        population.set(index, cloned_Elite_Individuals.get(clonedIndvIndex));
                        clonedIndvIndex--;
                    }

                    if (clonedIndvIndex != -1) { // we still have some cloned individuals to place into population
                        population.sort(Solution.ascendingOrderFitness); // sort so that worst solutions come first
                        for (int j = 0; (clonedIndvIndex != -1); j++) {
                            population.set(j, cloned_Elite_Individuals.get(clonedIndvIndex)); // set the worst solution to leftover children
                            clonedIndvIndex--;
                        }
                    }

                    // after each generation passes, increment life span of every individual in the population.*/
                    for (Solution sol : population) {
                        sol.incrementLifeSpan();
                    }

                    /** STEP 9. Update the best solution.*/

                    population.sort(new SolutionComparator()); // sort in descending order of fitness
                    if (best_Solution.fitnessValue < population.getFirst().fitnessValue) {
                        best_Solution = population.getFirst(); // store the best solution -> the first one
                    }

                    // clear all the arrays before next generation
                    duplicateSolutionIndexes.clear();
                    childrenAfterCrossOver.clear();
                    GSolutionIDs.clear();
                    RSolutionIDs.clear();
                    BSolutionIDs.clear();
                    G.clear();
                    R.clear();
                    PwithoutB.clear();
                    eliteGroupB.clear();
                    top20.clear();
                    childIDs.clear();
                    cloned_Elite_Individuals.clear();
                }
                long endTime = System.nanoTime();
                long executionTime = endTime - startTime;

                System.out.println("Final Best Solution for instance " + (instance + 1) + ": ");
                int bin_num = best_Solution.printSolutionInfo();
                System.out.println("Time taken for packing: " + executionTime + " nanoseconds.");
//                sumTime+=executionTime;

                //adding to the problem instances bin array which stores the number of bins for 30 runs
//                binRuns.add(bin_num);

            }

//            System.out.println("Average Time: "+((double)sumTime/30));
//            avgTime.add(((double)sumTime/30));

            //printing 30 bin packings after 30 runs
//            System.out.println("instance "+(instance+1)+binRuns);
//            writer.write("instance "+(instance+1)+"\n"+binRuns + "\n");
//            binRuns.clear();
//        }
//        System.out.println("avgtime: "+avgTime);
//        writer.close();
    }

}

