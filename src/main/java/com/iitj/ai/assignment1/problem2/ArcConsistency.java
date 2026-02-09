import java.util.*;

/**
 * AC-3 (Arc Consistency Algorithm 3) for the Security Bot Scheduling CSP.
 *
 * Enforces arc consistency by iteratively removing values from domains
 * that cannot participate in any consistent assignment with neighboring
 * variables. An arc (Xi, Xj) is consistent if for every value in Di,
 * there exists at least one value in Dj that satisfies the constraint.
 *
 * After AC-3 preprocessing, Backtracking with MRV is run to find a solution.
 *
 * Student : Suresh Babu Gandla | Roll No : 123245
 * IIT Jodhpur - M.Tech Programme
 */
public class ArcConsistency {

    /**
     * Run AC-3 algorithm on the CSP domains.
     * Binary constraint: No Back-to-Back (Slot[i] != Slot[i+1]).
     *
     * @param domains mutable list of domains for each slot
     * @return true if arc-consistent (no empty domains), false if inconsistent
     */
    static boolean ac3(List<List<String>> domains) {
        // Build queue of all arcs (directed edges)
        // Arcs: (i, i+1) and (i+1, i) for all consecutive slots
        Queue<int[]> queue = new LinkedList<>();
        for (int i = 0; i < CSPFramework.NUM_SLOTS - 1; i++) {
            queue.add(new int[]{i, i + 1});
            queue.add(new int[]{i + 1, i});
        }

        int revisionsCount = 0;

        while (!queue.isEmpty()) {
            int[] arc = queue.poll();
            int xi = arc[0], xj = arc[1];

            if (revise(domains, xi, xj)) {
                revisionsCount++;
                System.out.println("  Revised " + CSPFramework.SLOT_NAMES[xi]
                        + " -> " + CSPFramework.SLOT_NAMES[xj]
                        + " : " + CSPFramework.SLOT_NAMES[xi]
                        + " domain = " + domains.get(xi));

                if (domains.get(xi).isEmpty()) {
                    System.out.println("  FAILURE: " + CSPFramework.SLOT_NAMES[xi]
                            + " has empty domain!");
                    return false;
                }

                // Add all arcs (Xk, Xi) where Xk is a neighbor of Xi, Xk != Xj
                for (int xk = 0; xk < CSPFramework.NUM_SLOTS; xk++) {
                    if (xk != xj && Math.abs(xk - xi) == 1) {
                        queue.add(new int[]{xk, xi});
                    }
                }
            }
        }

        System.out.println("  Total revisions performed: " + revisionsCount);
        return true;
    }

    /**
     * Revise the domain of Xi to be arc-consistent with Xj.
     * Removes values from Di that have no support in Dj.
     * Constraint: Xi != Xj (No Back-to-Back).
     *
     * @return true if any value was removed from Di
     */
    static boolean revise(List<List<String>> domains, int xi, int xj) {
        boolean revised = false;
        List<String> toRemove = new ArrayList<>();

        for (String val : domains.get(xi)) {
            // Check if there exists at least one value in Dj != val
            boolean hasSupport = false;
            for (String other : domains.get(xj)) {
                if (!other.equals(val)) {
                    hasSupport = true;
                    break;
                }
            }
            if (!hasSupport) {
                toRemove.add(val);
                revised = true;
            }
        }

        domains.get(xi).removeAll(toRemove);
        return revised;
    }

    // -------------------- Main --------------------

    public static void main(String[] args) throws Exception {
        String inputFile = args.length > 0 ? args[0] : "input.txt";
        CSPFramework.readInput(inputFile);

        System.out.println("#".repeat(55));
        System.out.println("# SECURITY BOT SCHEDULING - CSP");
        System.out.println("# AC-3 Arc Consistency + Backtracking");
        System.out.println("#".repeat(55));
        System.out.println();

        // Print constraint graph
        CSPFramework.printConstraintGraph();

        // Initialize domains
        List<List<String>> domains = CSPFramework.initDomains();
        System.out.println("Initial Domains (after unary constraint):");
        for (int i = 0; i < CSPFramework.NUM_SLOTS; i++) {
            System.out.println("  " + CSPFramework.SLOT_NAMES[i] + " = " + domains.get(i));
        }
        System.out.println();

        // Run AC-3
        System.out.println("=== Running AC-3 Arc Consistency ===");
        System.out.println("Arcs to process: (Slot1,Slot2), (Slot2,Slot1), "
                + "(Slot2,Slot3), (Slot3,Slot2), (Slot3,Slot4), (Slot4,Slot3)");
        System.out.println();

        long startTime = System.currentTimeMillis();
        boolean consistent = ac3(domains);

        System.out.println();
        System.out.println("Domains after AC-3:");
        for (int i = 0; i < CSPFramework.NUM_SLOTS; i++) {
            System.out.println("  " + CSPFramework.SLOT_NAMES[i] + " = " + domains.get(i));
        }
        System.out.println("Arc Consistent: " + (consistent ? "YES" : "NO"));
        System.out.println();

        if (consistent) {
            // Now run backtracking on the reduced domains
            System.out.println("=== Running Backtracking on AC-3 Reduced Domains ===\n");
            String[] assignment = new String[CSPFramework.NUM_SLOTS];
            int[] count = {0};

            boolean success = backtrackSimple(assignment, domains, count);

            long timeMs = System.currentTimeMillis() - startTime;

            CSPFramework.printResult(
                    "AC-3 + Backtracking",
                    "MRV (Minimum Remaining Values)",
                    "AC-3 Arc Consistency Preprocessing",
                    success, assignment, count[0], timeMs);
        } else {
            long timeMs = System.currentTimeMillis() - startTime;
            System.out.println("CSP is INCONSISTENT - no solution exists.");
            System.out.println("Time: " + timeMs + " ms");
        }
    }

    /**
     * Simple backtracking with MRV on AC-3 reduced domains.
     */
    static boolean backtrackSimple(String[] assignment, List<List<String>> domains,
                                    int[] count) {
        boolean complete = true;
        for (String s : assignment) {
            if (s == null) { complete = false; break; }
        }
        if (complete) return CSPFramework.checkMinimumCoverage(assignment);

        // MRV: select variable with fewest remaining values
        int slot = -1;
        int minSize = Integer.MAX_VALUE;
        for (int i = 0; i < CSPFramework.NUM_SLOTS; i++) {
            if (assignment[i] == null && domains.get(i).size() < minSize) {
                minSize = domains.get(i).size();
                slot = i;
            }
        }
        if (slot == -1) return false;

        for (String bot : new ArrayList<>(domains.get(slot))) {
            if (CSPFramework.isConsistent(assignment, slot, bot)) {
                count[0]++;
                assignment[slot] = bot;
                if (CSPFramework.canSatisfyCoverage(assignment, domains)) {
                    if (backtrackSimple(assignment, domains, count)) return true;
                }
                assignment[slot] = null;
            }
        }
        return false;
    }
}
