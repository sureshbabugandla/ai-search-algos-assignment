import java.util.*;

/**
 * Iterative Deepening A* (IDA*) for the Manuscript Sorting Problem.
 *
 * Combines A* optimality with DFS memory efficiency.
 * Performs iterative depth-limited DFS using f(n) = g(n) + h(n) as cutoff.
 * Threshold increases to the minimum f-value exceeding the previous bound.
 * Uses path-based cycle detection (not a global visited set) to save memory.
 *
 * Time Complexity : O(b^d) - but re-expands states across iterations
 * Space Complexity: O(b*d) - linear in depth (only current path stored)
 * Optimal         : Yes (with admissible heuristic)
 *
 * Student : Suresh Babu Gandla | Roll No : 123245
 * IIT Jodhpur - M.Tech Programme
 */
public class IDAStarSearch {

    private static int statesExplored;

    /**
     * Recursive DFS with f-value threshold.
     * Returns: -1 if FOUND, otherwise the minimum f exceeding threshold.
     */
    static int idaSearch(List<int[]> path, Set<String> pathSet,
                         int g, int threshold, boolean useH1) {
        int[] current = path.get(path.size() - 1);
        int h = useH1 ? PuzzleState.h1(current) : PuzzleState.h2(current);
        int f = g + h;

        if (f > threshold) return f;        // Exceeded threshold
        statesExplored++;
        if (PuzzleState.isGoal(current)) return -1;  // FOUND

        int min = Integer.MAX_VALUE;
        for (int[] neighbor : PuzzleState.getNeighbors(current)) {
            String nKey = Arrays.toString(neighbor);
            if (!pathSet.contains(nKey)) {  // Cycle detection on current path
                path.add(neighbor);
                pathSet.add(nKey);

                int result = idaSearch(path, pathSet, g + 1, threshold, useH1);

                if (result == -1) return -1;  // Found solution
                if (result < min) min = result;

                path.remove(path.size() - 1);
                pathSet.remove(nKey);
            }
        }
        return min;
    }

    /**
     * Run IDA* with specified heuristic.
     * @param useH1 true = h1 (misplaced tiles), false = h2 (Manhattan distance)
     */
    static void runIDAStar(int[] initial, boolean useH1) {
        String hName = useH1 ? "h1 - Misplaced Tiles" : "h2 - Manhattan Distance";
        long startTime = System.currentTimeMillis();
        statesExplored = 0;
        boolean success = false;
        List<int[]> solutionPath = null;

        int threshold = useH1 ? PuzzleState.h1(initial) : PuzzleState.h2(initial);
        List<int[]> path = new ArrayList<>();
        path.add(initial);
        Set<String> pathSet = new HashSet<>();
        pathSet.add(Arrays.toString(initial));

        int iteration = 0;
        while (true) {
            iteration++;
            int result = idaSearch(path, pathSet, 0, threshold, useH1);

            if (result == -1) {
                // Solution found - path contains the solution
                success = true;
                solutionPath = new ArrayList<>(path);
                break;
            }
            if (result == Integer.MAX_VALUE) {
                // No solution exists
                break;
            }

            System.out.println("  IDA* iteration " + iteration
                    + ": threshold=" + threshold + " -> next=" + result
                    + " (states so far: " + statesExplored + ")");
            threshold = result;  // Increase threshold to next smallest f
        }

        long timeMs = System.currentTimeMillis() - startTime;

        PuzzleState.printResult("Iterative Deepening A* (IDA*)", hName,
                success, solutionPath, statesExplored, timeMs);
        System.out.println("Total IDA* iterations: " + iteration);
        System.out.println();
    }

    public static void main(String[] args) throws Exception {
        String inputFile = args.length > 0 ? args[0] : "input.txt";
        int[][] input = PuzzleState.readInput(inputFile);
        int[] initial = input[0];

        System.out.println("Start State: " + PuzzleState.stateToString(initial));
        System.out.println("Goal  State: " + PuzzleState.stateToString(PuzzleState.GOAL));
        System.out.println("Start Grid:\n" + PuzzleState.stateToGrid(initial));

        // Run IDA* with h1 (Misplaced Tiles)
        runIDAStar(initial, true);

        // Run IDA* with h2 (Manhattan Distance)
        runIDAStar(initial, false);
    }
}
