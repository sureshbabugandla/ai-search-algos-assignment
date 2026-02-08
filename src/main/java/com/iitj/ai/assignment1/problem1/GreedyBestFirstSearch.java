import java.util.*;

/**
 * Greedy Best-First Search for the Manuscript Sorting Problem.
 *
 * Uses f(n) = h(n) only (ignores path cost g(n)).
 * Prioritizes states with smallest heuristic value (h2 - Manhattan Distance).
 * Fast but NOT guaranteed to find the optimal solution.
 *
 * Time Complexity : O(b^m) worst case
 * Space Complexity: O(b^m) - stores frontier and visited set
 * Optimal         : No
 *
 * Student : Suresh Babu Gandla | Roll No : 123245
 * IIT Jodhpur - M.Tech Programme
 */
public class GreedyBestFirstSearch {

    // Node for priority queue: ordered by heuristic h only
    static class Node implements Comparable<Node> {
        int[] state;
        int h;
        String key;

        Node(int[] state, int h) {
            this.state = state;
            this.h = h;
            this.key = Arrays.toString(state);
        }

        public int compareTo(Node o) {
            return Integer.compare(this.h, o.h);
        }
    }

    public static void main(String[] args) throws Exception {
        String inputFile = args.length > 0 ? args[0] : "input.txt";
        int[][] input = PuzzleState.readInput(inputFile);
        int[] initial = input[0];

        System.out.println("Start State: " + PuzzleState.stateToString(initial));
        System.out.println("Goal  State: " + PuzzleState.stateToString(PuzzleState.GOAL));
        System.out.println("Start Grid:\n" + PuzzleState.stateToGrid(initial));

        // Run Greedy Best-First Search
        long startTime = System.currentTimeMillis();
        int statesExplored = 0;
        boolean success = false;
        List<int[]> solutionPath = null;

        PriorityQueue<Node> frontier = new PriorityQueue<>();
        Map<String, String> parent = new HashMap<>();
        Map<String, int[]> stateMap = new HashMap<>();
        Set<String> visited = new HashSet<>();

        String initKey = Arrays.toString(initial);
        frontier.add(new Node(initial, PuzzleState.h2(initial)));
        parent.put(initKey, null);
        stateMap.put(initKey, initial);

        while (!frontier.isEmpty()) {
            Node node = frontier.poll();

            if (visited.contains(node.key)) continue;
            visited.add(node.key);
            statesExplored++;

            // Goal test
            if (PuzzleState.isGoal(node.state)) {
                success = true;
                solutionPath = PuzzleState.reconstructPath(parent, stateMap, node.state);
                break;
            }

            // Expand: prioritize by h(n) only
            for (int[] neighbor : PuzzleState.getNeighbors(node.state)) {
                String nKey = Arrays.toString(neighbor);
                if (!visited.contains(nKey)) {
                    if (!parent.containsKey(nKey)) {
                        parent.put(nKey, node.key);
                        stateMap.put(nKey, neighbor);
                    }
                    frontier.add(new Node(neighbor, PuzzleState.h2(neighbor)));
                }
            }
        }

        long timeMs = System.currentTimeMillis() - startTime;

        PuzzleState.printResult("Greedy Best-First Search",
                "h2 - Manhattan Distance",
                success, solutionPath, statesExplored, timeMs);
    }
}
