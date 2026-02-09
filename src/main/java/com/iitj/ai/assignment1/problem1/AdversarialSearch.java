import java.util.*;

/**
 * Adversarial Search for the Manuscript Sorting Problem.
 * Implements both Minimax and Alpha-Beta Pruning.
 *
 * Two-player adversarial formulation:
 *   MAX player (Robotic Sorter) : tries to reach goal (maximize utility)
 *   MIN player (System Glitch)  : tries to increase disorder (minimize utility)
 *   Utility function: u(s) = -ManhattanDistance(s)
 *     Higher (closer to 0) = better for MAX
 *     Lower (more negative) = better for MIN
 *
 * Alpha-Beta Pruning: prunes branches where beta <= alpha,
 *   returning the same result as Minimax with fewer state evaluations.
 *
 * Student : Suresh Babu Gandla | Roll No : 123245
 * IIT Jodhpur - M.Tech Programme
 */
public class AdversarialSearch {

    private static int minimaxCalls = 0;
    private static int alphaBetaCalls = 0;

    // -------------------- Utility Function --------------------

    /**
     * Utility = negative Manhattan distance.
     * MAX wants this high (close to 0 = near goal).
     * MIN wants this low (far from goal).
     */
    static int utility(int[] state) {
        return -PuzzleState.h2(state);
    }

    // -------------------- Minimax --------------------

    /**
     * Plain Minimax search.
     * @param state   current game state
     * @param depth   remaining depth to search
     * @param isMax   true if MAX's turn, false if MIN's turn
     * @param visited set of visited states (prevent cycles)
     * @return minimax value
     */
    static int minimax(int[] state, int depth, boolean isMax, Set<String> visited) {
        minimaxCalls++;

        // Terminal test: depth exhausted or goal reached
        if (depth == 0 || PuzzleState.isGoal(state)) {
            return utility(state);
        }

        List<int[]> neighbors = PuzzleState.getNeighbors(state);

        if (isMax) {
            // MAX player: choose move that maximizes utility
            int best = Integer.MIN_VALUE;
            for (int[] next : neighbors) {
                String key = Arrays.toString(next);
                if (!visited.contains(key)) {
                    visited.add(key);
                    int val = minimax(next, depth - 1, false, visited);
                    best = Math.max(best, val);
                    visited.remove(key);
                }
            }
            return best == Integer.MIN_VALUE ? utility(state) : best;
        } else {
            // MIN player: choose move that minimizes utility
            int worst = Integer.MAX_VALUE;
            for (int[] next : neighbors) {
                String key = Arrays.toString(next);
                if (!visited.contains(key)) {
                    visited.add(key);
                    int val = minimax(next, depth - 1, true, visited);
                    worst = Math.min(worst, val);
                    visited.remove(key);
                }
            }
            return worst == Integer.MAX_VALUE ? utility(state) : worst;
        }
    }

    // -------------------- Alpha-Beta Pruning --------------------

    /**
     * Minimax with Alpha-Beta pruning.
     * @param alpha best value MAX can guarantee (lower bound)
     * @param beta  best value MIN can guarantee (upper bound)
     * Prunes when beta <= alpha (remaining branches cannot affect decision).
     */
    static int alphaBeta(int[] state, int depth, int alpha, int beta,
                         boolean isMax, Set<String> visited) {
        alphaBetaCalls++;

        if (depth == 0 || PuzzleState.isGoal(state)) {
            return utility(state);
        }

        List<int[]> neighbors = PuzzleState.getNeighbors(state);

        if (isMax) {
            int best = Integer.MIN_VALUE;
            for (int[] next : neighbors) {
                String key = Arrays.toString(next);
                if (!visited.contains(key)) {
                    visited.add(key);
                    int val = alphaBeta(next, depth - 1, alpha, beta, false, visited);
                    best = Math.max(best, val);
                    visited.remove(key);
                    alpha = Math.max(alpha, best);
                    if (beta <= alpha) break;  // Beta cutoff - prune
                }
            }
            return best == Integer.MIN_VALUE ? utility(state) : best;
        } else {
            int worst = Integer.MAX_VALUE;
            for (int[] next : neighbors) {
                String key = Arrays.toString(next);
                if (!visited.contains(key)) {
                    visited.add(key);
                    int val = alphaBeta(next, depth - 1, alpha, beta, true, visited);
                    worst = Math.min(worst, val);
                    visited.remove(key);
                    beta = Math.min(beta, worst);
                    if (beta <= alpha) break;  // Alpha cutoff - prune
                }
            }
            return worst == Integer.MAX_VALUE ? utility(state) : worst;
        }
    }

    // -------------------- Run and Compare --------------------

    /**
     * Find best move for MAX using Minimax.
     */
    static int[] runMinimax(int[] initial, int depth) {
        minimaxCalls = 0;
        Set<String> visited = new HashSet<>();
        visited.add(Arrays.toString(initial));
        int bestVal = Integer.MIN_VALUE;
        int[] bestMove = null;
        String bestAction = "";

        for (int[] next : PuzzleState.getNeighbors(initial)) {
            String key = Arrays.toString(next);
            visited.add(key);
            int val = minimax(next, depth - 1, false, visited);
            visited.remove(key);
            if (val > bestVal) {
                bestVal = val;
                bestMove = next;
                bestAction = PuzzleState.getAction(initial, next);
            }
        }

        System.out.println("  Best move: " + bestAction + " (utility=" + bestVal + ")");
        return bestMove;
    }

    /**
     * Find best move for MAX using Alpha-Beta.
     */
    static int[] runAlphaBeta(int[] initial, int depth) {
        alphaBetaCalls = 0;
        Set<String> visited = new HashSet<>();
        visited.add(Arrays.toString(initial));
        int bestVal = Integer.MIN_VALUE;
        int[] bestMove = null;
        String bestAction = "";

        for (int[] next : PuzzleState.getNeighbors(initial)) {
            String key = Arrays.toString(next);
            visited.add(key);
            int val = alphaBeta(next, depth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE,
                                false, visited);
            visited.remove(key);
            if (val > bestVal) {
                bestVal = val;
                bestMove = next;
                bestAction = PuzzleState.getAction(initial, next);
            }
        }

        System.out.println("  Best move: " + bestAction + " (utility=" + bestVal + ")");
        return bestMove;
    }

    // -------------------- Main --------------------

    public static void main(String[] args) throws Exception {
        String inputFile = args.length > 0 ? args[0] : "input1.txt";
        int[][] input = PuzzleState.readInput(inputFile);
        int[] initial = input[0];

        System.out.println("Start State: " + PuzzleState.stateToString(initial));
        System.out.println("Goal  State: " + PuzzleState.stateToString(PuzzleState.GOAL));
        System.out.println("Start Grid:\n" + PuzzleState.stateToGrid(initial));

        int searchDepth = 6;
        System.out.println("Adversarial Search Depth: " + searchDepth);
        System.out.println("Utility function: u(s) = -ManhattanDistance(s)");
        System.out.println();

        // ---- Minimax ----
        System.out.println("--- Plain Minimax ---");
        long t1 = System.currentTimeMillis();
        int[] mmMove = runMinimax(initial, searchDepth);
        long mmTime = System.currentTimeMillis() - t1;
        int mmStates = minimaxCalls;
        System.out.println("  States evaluated: " + mmStates);
        System.out.println("  Time: " + mmTime + " ms");
        if (mmMove != null) {
            System.out.println("  Resulting state:");
            System.out.print(PuzzleState.stateToGrid(mmMove));
        }
        System.out.println();

        // ---- Alpha-Beta ----
        System.out.println("--- Alpha-Beta Pruning ---");
        long t2 = System.currentTimeMillis();
        int[] abMove = runAlphaBeta(initial, searchDepth);
        long abTime = System.currentTimeMillis() - t2;
        int abStates = alphaBetaCalls;
        System.out.println("  States evaluated: " + abStates);
        System.out.println("  Time: " + abTime + " ms");
        if (abMove != null) {
            System.out.println("  Resulting state:");
            System.out.print(PuzzleState.stateToGrid(abMove));
        }
        System.out.println();

        // ---- Comparison ----
        System.out.println("=".repeat(60));
        System.out.println("COMPARISON: Minimax vs Alpha-Beta (depth=" + searchDepth + ")");
        System.out.println("=".repeat(60));
        System.out.println("                    Minimax    Alpha-Beta");
        System.out.println("States evaluated:   " + String.format("%-11d%d", mmStates, abStates));
        System.out.println("Time (ms):          " + String.format("%-11d%d", mmTime, abTime));
        System.out.println("Same best move?     "
                + (Arrays.equals(mmMove, abMove) ? "YES (pruning is lossless)" : "NO (unexpected)"));
        if (mmStates > 0) {
            double savings = (1.0 - (double) abStates / mmStates) * 100;
            System.out.printf("Pruning saved:      %.1f%% of state evaluations%n", savings);
        }
        System.out.println();
    }
}
