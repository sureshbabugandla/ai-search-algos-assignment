import java.util.*;

/**
 * Depth-First Search (DFS) for the Manuscript Sorting Problem.
 *
 * DFS explores deeply using a LIFO stack. To prevent infinite paths:
 *   1. Depth limit of 50 (above max optimal depth of 31)
 *   2. Visited set to eliminate cycles
 * DFS is NOT guaranteed to find the optimal solution.
 *
 * Time Complexity : O(b^m) where m=max depth
 * Space Complexity: O(b*m) - stores only current path + siblings
 * Optimal         : No
 *
 * Student : Suresh Babu Gandla | Roll No : 123245
 * IIT Jodhpur - M.Tech Programme
 */
public class DFSSearch {

    private static final int DEPTH_LIMIT = 50;

    public static void main(String[] args) throws Exception {
        String inputFile = args.length > 0 ? args[0] : "input1.txt";
        int[][] input = PuzzleState.readInput(inputFile);
        int[] initial = input[0];

        System.out.println("Start State: " + PuzzleState.stateToString(initial));
        System.out.println("Goal  State: " + PuzzleState.stateToString(PuzzleState.GOAL));
        System.out.println("Start Grid:\n" + PuzzleState.stateToGrid(initial));

        // Run DFS
        long startTime = System.currentTimeMillis();
        int statesExplored = 0;
        boolean success = false;
        List<int[]> solutionPath = null;

        // Frontier: LIFO stack
        Deque<int[]> frontier = new ArrayDeque<>();
        Deque<Integer> depths = new ArrayDeque<>();
        // Parent map for path reconstruction
        Map<String, String> parent = new HashMap<>();
        Map<String, int[]> stateMap = new HashMap<>();
        // Visited set: prevents cycles and revisiting root/parent/explored states
        Set<String> visited = new HashSet<>();

        String initKey = Arrays.toString(initial);
        frontier.push(initial);
        depths.push(0);
        parent.put(initKey, null);
        stateMap.put(initKey, initial);

        while (!frontier.isEmpty()) {
            int[] current = frontier.pop();
            int depth = depths.pop();
            String curKey = Arrays.toString(current);

            // Skip if already visited (cycle detection)
            if (visited.contains(curKey)) continue;
            visited.add(curKey);
            statesExplored++;

            // Goal test
            if (PuzzleState.isGoal(current)) {
                success = true;
                solutionPath = PuzzleState.reconstructPath(parent, stateMap, current);
                break;
            }

            // Depth bound: prevent infinite paths
            if (depth >= DEPTH_LIMIT) continue;

            // Expand neighbors
            for (int[] neighbor : PuzzleState.getNeighbors(current)) {
                String nKey = Arrays.toString(neighbor);
                if (!visited.contains(nKey)) {
                    parent.put(nKey, curKey);
                    stateMap.put(nKey, neighbor);
                    frontier.push(neighbor);
                    depths.push(depth + 1);
                }
            }
        }

        long timeMs = System.currentTimeMillis() - startTime;

        PuzzleState.printResult("Depth-First Search (DFS)",
                "Depth Limit = " + DEPTH_LIMIT,
                success, solutionPath, statesExplored, timeMs);
    }
}
