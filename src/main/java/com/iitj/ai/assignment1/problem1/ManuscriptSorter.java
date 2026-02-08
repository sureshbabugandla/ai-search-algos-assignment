import java.util.*;

/**
 * ManuscriptSorter - Master runner for all search algorithms.
 * Runs all 8 algorithms on the input state and prints a comparison summary.
 *
 * Usage: javac *.java && java ManuscriptSorter [input.txt]
 *
 * Student : Suresh Babu Gandla | Roll No : 123245
 * IIT Jodhpur - M.Tech Programme
 */
public class ManuscriptSorter {

    public static void main(String[] args) throws Exception {
        String inputFile = args.length > 0 ? args[0] : "input.txt";
        int[][] input = PuzzleState.readInput(inputFile);
        int[] initial = input[0];

        System.out.println("#".repeat(60));
        System.out.println("#  MANUSCRIPT SORTING PROBLEM - COMPLETE ANALYSIS");
        System.out.println("#  Student: Suresh Babu Gandla (123245)");
        System.out.println("#  IIT Jodhpur - M.Tech Programme");
        System.out.println("#".repeat(60));
        System.out.println();
        System.out.println("Start State: " + PuzzleState.stateToString(initial));
        System.out.println("Goal  State: " + PuzzleState.stateToString(PuzzleState.GOAL));
        System.out.println("Start Grid:\n" + PuzzleState.stateToGrid(initial));
        System.out.println("h1 (Misplaced Tiles)  = " + PuzzleState.h1(initial));
        System.out.println("h2 (Manhattan Distance) = " + PuzzleState.h2(initial));
        System.out.println();

        // Execute each algorithm's main method
        System.out.println("*".repeat(60));
        System.out.println("*  SECTION 2A: UNINFORMED SEARCH");
        System.out.println("*".repeat(60));
        System.out.println();
        BFSSearch.main(new String[]{inputFile});
        DFSSearch.main(new String[]{inputFile});

        System.out.println("*".repeat(60));
        System.out.println("*  SECTION 2B: INFORMED SEARCH");
        System.out.println("*".repeat(60));
        System.out.println();
        GreedyBestFirstSearch.main(new String[]{inputFile});
        AStarSearch.main(new String[]{inputFile});

        System.out.println("*".repeat(60));
        System.out.println("*  SECTION 2C: MEMORY-BOUNDED & LOCAL SEARCH");
        System.out.println("*".repeat(60));
        System.out.println();
        IDAStarSearch.main(new String[]{inputFile});
        SimulatedAnnealingSearch.main(new String[]{inputFile});

        System.out.println("*".repeat(60));
        System.out.println("*  SECTION 2D: ADVERSARIAL SEARCH");
        System.out.println("*".repeat(60));
        System.out.println();
        AdversarialSearch.main(new String[]{inputFile});
    }
}
