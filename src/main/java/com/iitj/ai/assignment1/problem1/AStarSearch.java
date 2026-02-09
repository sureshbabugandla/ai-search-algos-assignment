import java.util.*;

/**
 * A* Search for the Manuscript Sorting Problem.
 *
 * Uses f(n) = g(n) + h(n), combining actual path cost with heuristic estimate.
 * Runs with two heuristics:
 *   h1: Number of misplaced manuscripts
 *   h2: Total Manhattan Distance (dominates h1)
 * Optimal when heuristic is admissible. Both h1 and h2 are admissible.
 *
 * Time Complexity : O(b^d) - depends on heuristic quality
 * Space Complexity: O(b^d) - stores all generated states
 * Optimal         : Yes (with admissible heuristic)
 *
 * Student : Suresh Babu Gandla | Roll No : 123245
 * IIT Jodhpur - M.Tech Programme
 */
public class AStarSearch {

    // Node for priority queue: ordered by f = g + h
    static class Node implements Comparable<Node> {
        int[] state;
        int g;  // path cost
        int h;  // heuristic value
        int f;  // total estimated cost
        String key;

        Node(int[] state, int g, int h) {
            this.state = state;
            this.g = g;
            this.h = h;
            this.f = g + h;
            this.key = Arrays.toString(state);
        }

        public int compareTo(Node o) {
            if (this.f != o.f) return Integer.compare(this.f, o.f);
            return Integer.compare(this.h, o.h); // tie-break: prefer lower h
        }
    }

    /**
     * Run A* with specified heuristic.
     * @param useH1 true = h1 (misplaced tiles), false = h2 (Manhattan distance)
     */
    static void runAStar(int[] initial, boolean useH1) {
        String hName = useH1 ? "h1 - Misplaced Tiles" : "h2 - Manhattan Distance";
        long startTime = System.currentTimeMillis();
        int statesExplored = 0;
        boolean success = false;
        List<int[]> solutionPath = null;

        PriorityQueue<Node> frontier = new PriorityQueue<>();
        Map<String, Integer> bestG = new HashMap<>(); // best g-value found for each state
        Map<String, String> parent = new HashMap<>();
        Map<String, int[]> stateMap = new HashMap<>();

        String initKey = Arrays.toString(initial);
        int hVal = useH1 ? PuzzleState.h1(initial) : PuzzleState.h2(initial);
        frontier.add(new Node(initial, 0, hVal));
        bestG.put(initKey, 0);
        parent.put(initKey, null);
        stateMap.put(initKey, initial);

        while (!frontier.isEmpty()) {
            Node node = frontier.poll();
            statesExplored++;

            // Goal test
            if (PuzzleState.isGoal(node.state)) {
                success = true;
                solutionPath = PuzzleState.reconstructPath(parent, stateMap, node.state);
                break;
            }

            // Skip if we already found a better path to this state
            if (node.g > bestG.getOrDefault(node.key, Integer.MAX_VALUE)) {
                continue;
            }

            // Expand neighbors
            for (int[] neighbor : PuzzleState.getNeighbors(node.state)) {
                String nKey = Arrays.toString(neighbor);
                int newG = node.g + 1; // each move costs 1 unit of System Energy

                if (newG < bestG.getOrDefault(nKey, Integer.MAX_VALUE)) {
                    bestG.put(nKey, newG);
                    parent.put(nKey, node.key);
                    stateMap.put(nKey, neighbor);
                    int nh = useH1 ? PuzzleState.h1(neighbor) : PuzzleState.h2(neighbor);
                    frontier.add(new Node(neighbor, newG, nh));
                }
            }
        }

        long timeMs = System.currentTimeMillis() - startTime;

        PuzzleState.printResult("A* Search", hName,
                success, solutionPath, statesExplored, timeMs);
    }

    public static void main(String[] args) throws Exception {
        String inputFile = args.length > 0 ? args[0] : "input1.txt";
        int[][] input = PuzzleState.readInput(inputFile);
        int[] initial = input[0];

        System.out.println("Start State: " + PuzzleState.stateToString(initial));
        System.out.println("Goal  State: " + PuzzleState.stateToString(PuzzleState.GOAL));
        System.out.println("Start Grid:\n" + PuzzleState.stateToGrid(initial));

        // Run A* with h1 (Misplaced Tiles)
        runAStar(initial, true);

        // Run A* with h2 (Manhattan Distance)
        runAStar(initial, false);
    }
}
