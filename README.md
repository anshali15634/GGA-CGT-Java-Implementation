# GGA-CGT: Java Implementation

This repository contains a Java implementation of the **Grouping Genetic Algorithm with Controlled Gene Transmission (GGA-CGT)** for solving the one-dimensional Bin Packing Problem (BPP). The BPP is a classical optimization problem, well-known for its complexity and wide applicability. The GGA-CGT algorithm, first introduced by Quiroz et al., enhances the traditional genetic algorithm by promoting the transmission of the best genes within chromosomes while maintaining a balance between selective pressure and population diversity. This balance is achieved through novel grouping genetic operators and a reproduction technique that controls the exploration of the search space, effectively preventing premature convergence.

## Prerequisites

Create a new IntelliJ Project named "COMP2024-CW-Group014". Place the GGACGT package directories and the `BPP.txt` file from the submitted folder into the new project's **src** folder.

Ensure the following project directory structure is maintained before running the algorithm:

```bash
> COMP2024-CW-Group014
 > src
  > GGACGT
    - adaptiveMutationOperatorAndRP.java
    - Bin.java
    - crossOverOperator.java
    - FFnHeuristic.java
    - lowerBoundCalculator.java
    - Main.java
    - problemInstance.java
    - ReadFile.java
    - Solution.java
    - SolutionComparator.java
  - BPP.txt
  README.md
```
## Usage

To run the GGA-CGT algorithm, execute the following file in the GGACGT package directory:

```bash
Main.java
```

Code by Anshali Manoharan

