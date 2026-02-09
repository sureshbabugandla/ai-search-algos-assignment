import java.util.*;

/**
 * Simulated Annealing for the Manuscript Sorting Problem.
 *
 * A probabilistic local search algorithm inspired by metallurgical annealing.
 * Uses Manhattan Distance (h2) as the energy function.
 *
 * Cooling Schedule:
 *   Initial Temperature T0 = 1000
 *   Cooling Rate alpha    = 0.9995 (geometric: T = T * alpha)
 *   Minimum Temperature   = 0.001
 *   Max Iterations         = 500,000
 *
 * Acceptance Probability: P = e^(-deltaE / T)
 *   - Always accepts improving moves (deltaE < 0)
 *   - Probabilistically accepts worse moves to escape local maxima
 *
 * Time Complexity : O(maxIterations)
 * Space Complexity: O(1) - only stores current state
 * Optimal         : No (stochastic, no guarantee)
 *
 * Student : Suresh Babu Gandla | Roll No : 123245
 * IIT Jodhpur - M.Tech Programme
 */
public class SimulatedAnnealingSearch {

    // Cooling schedule parameters
    private static final double INITIAL_TEMP = 1000.0;
    private static final double COOLING_RATE = 0.9995;
    private static final double MIN_TEMP = 0.001;
    private static final int MAX_ITERATIONS = 500000;

    public static void main(String[] args) throws Exception {
        String inputFile = args.length > 0 ? args[0] : "input1.txt";
        int[][] input = PuzzleState.readInput(inputFile);
        int[] initial = input[0];

        System.out.println("Start State: " + PuzzleState.stateToString(initial));
        System.out.println("Goal  State: " + PuzzleState.stateToString(PuzzleState.GOAL));
        System.out.println("Start Grid:\n" + PuzzleState.stateToGrid(initial));

        // Cooling schedule info
        System.out.println("Cooling Schedule:");
        System.out.println("  T0           = " + INITIAL_TEMP);
        System.out.println("  Cooling Rate = " + COOLING_RATE);
        System.out.println("  T_min        = " + MIN_TEMP);
        System.out.println("  Max Iter     = " + MAX_ITERATIONS);
        System.out.println();

        // Run Simulated Annealing
        long startTime = System.currentTimeMillis();
        Random rng = new Random(42); // fixed seed for reproducibility
        int statesExplored = 0;
        boolean success = false;

        double T = INITIAL_TEMP;
        int[] current = initial.clone();
        int currentH = PuzzleState.h2(current);

        // Track best state found
        int[] bestState = current.clone();
        int bestH = currentH;

        // Track path
        List<int[]> path = new ArrayList<>();
        path.add(initial.clone());

        for (int iter = 0; iter < MAX_ITERATIONS && T > MIN_TEMP; iter++) {
            statesExplored++;

            // Check if goal reached
            if (currentH == 0) {
                success = true;
                break;
            }

            // Select a random neighbor
            List<int[]> neighbors = PuzzleState.getNeighbors(current);
            int[] next = neighbors.get(rng.nextInt(neighbors.size()));
            int nextH = PuzzleState.h2(next);
            int deltaE = nextH - currentH; // positive = worse

            // Acceptance criterion: P = e^(-deltaE / T)
            if (deltaE < 0 || rng.nextDouble() < Math.exp(-deltaE / T)) {
                current = next;
                currentH = nextH;
                path.add(current.clone());

                if (currentH < bestH) {
                    bestH = currentH;
                    bestState = current.clone();
                }
            }

            // Cool down
            T *= COOLING_RATE;

            // Progress logging every 100,000 iterations
            if ((iter + 1) % 100000 == 0) {
                System.out.printf("  Iteration %d: T=%.4f, current h2=%d, best h2=%d%n",
                        iter + 1, T, currentH, bestH);
            }
        }

        long timeMs = System.currentTimeMillis() - startTime;

        // Print results
        System.out.println();
        PuzzleState.printResult("Simulated Annealing",
                "h2 - Manhattan Distance",
                success, success ? path : null, statesExplored, timeMs);

        System.out.println("Final Temperature: " + String.format("%.6f", T));
        System.out.println("Best h2 achieved : " + bestH);
        if (!success) {
            System.out.println("Best state found (not goal):");
            System.out.print(PuzzleState.stateToGrid(bestState));
        }
        System.out.println();
    }
}
