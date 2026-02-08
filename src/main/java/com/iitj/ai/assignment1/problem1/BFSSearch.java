import java.util.*;

/**
 * Breadth-First Search (BFS) for the Manuscript Sorting Problem.
 *
 * BFS explores states level-by-level using a FIFO queue, guaranteeing
 * the minimum number of moves (optimal for uniform step-cost problems).
 * A visited set prevents revisiting states.
 *
 * Time Complexity : O(b^d) where b=avg branching factor, d=solution depth
 * Space Complexity: O(b^d) - stores all explored states
 * Optimal         : Yes (uniform cost)
 *
 * Student : Suresh Babu Gandla | Roll No : 123245
 * IIT Jodhpur - M.Tech Programme
 */
public class BFSSearch {
    public static void main(String[] args) throws Exception {
        String inputFile = args.length > 0 ? args[0] : "input1.txt";
        int[][] input = PuzzleState.readInput(inputFile);
        int[] initial = input[0];

        System.out.println("Start State: " + PuzzleState.stateToString(initial));
        System.out.println("Goal  State: " + PuzzleState.stateToString(PuzzleState.GOAL));
        System.out.println("Start Grid:\n" + PuzzleState.stateToGrid(initial));

        // Run BFS
        long startTime = System.currentTimeMillis();
        int statesExplored = 0;
        boolean success = false;
        List<int[]> solutionPath = null;

        // Frontier: FIFO queue
        Queue<int[]> frontier = new LinkedList<>();
        // Parent map for path reconstruction
        Map<String, String> parent = new HashMap<>();
        // State map: key -> state array
        Map<String, int[]> stateMap = new HashMap<>();
        // Visited set to avoid revisiting states
        Set<String> visited = new HashSet<>();

        String initKey = Arrays.toString(initial);
        frontier.add(initial);
        visited.add(initKey);
        parent.put(initKey, null);
        stateMap.put(initKey, initial);

        while (!frontier.isEmpty()) {
            int[] current = frontier.poll();
            String curKey = Arrays.toString(current);
            statesExplored++;

            // Goal test
            if (PuzzleState.isGoal(current)) {
                success = true;
                solutionPath = PuzzleState.reconstructPath(parent, stateMap, current);
                break;
            }

            // Expand neighbors
            for (int[] neighbor : PuzzleState.getNeighbors(current)) {
                String nKey = Arrays.toString(neighbor);
                if (!visited.contains(nKey)) {
                    visited.add(nKey);
                    parent.put(nKey, curKey);
                    stateMap.put(nKey, neighbor);
                    frontier.add(neighbor);
                }
            }
        }

        long timeMs = System.currentTimeMillis() - startTime;

        // Print results
        PuzzleState.printResult("Breadth-First Search (BFS)", "",
                success, solutionPath, statesExplored, timeMs);
    }
}
