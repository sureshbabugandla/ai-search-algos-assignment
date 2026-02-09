import java.util.*;

/**
 * Backtracking Search with MRV Heuristic and Forward Checking
 * for the Security Bot Scheduling CSP.
 *
 * MRV (Minimum Remaining Values): Select the unassigned variable with
 * the fewest legal values remaining in its domain. This is the most
 * constrained variable, failing early if it has no options.
 *
 * Forward Checking: After each assignment, prune inconsistent values
 * from the domains of unassigned neighbors. If any domain becomes
 * empty, backtrack immediately without further exploration.
 *
 * Student : Suresh Babu Gandla | Roll No : 123245
 * IIT Jodhpur - M.Tech Programme
 */
public class BacktrackingSearch {

    private static int totalAssignments = 0;
    private static int stepCount = 0;

    // -------------------- MRV Heuristic --------------------

    /**
     * Select the unassigned slot with the Minimum Remaining Values.
     * Returns -1 if all slots are assigned.
     */
    static int selectMRV(String[] assignment, List<List<String>> domains) {
        int minSize = Integer.MAX_VALUE;
        int bestSlot = -1;

        for (int i = 0; i < CSPFramework.NUM_SLOTS; i++) {
            if (assignment[i] == null) {
                int domSize = domains.get(i).size();
                if (domSize < minSize) {
                    minSize = domSize;
                    bestSlot = i;
                }
            }
        }
        return bestSlot;
    }

    // -------------------- Forward Checking --------------------

    /**
     * Perform Forward Checking after assigning bot to slot.
     * Removes inconsistent values from neighboring unassigned variables.
     * Returns the list of pruned (slot, value) pairs for undo, or null if failure.
     */
    static List<int[]> forwardCheck(String[] assignment, List<List<String>> domains,
                                     int slot, String bot) {
        List<int[]> pruned = new ArrayList<>();

        // Check left neighbor (slot - 1)
        if (slot > 0 && assignment[slot - 1] == null) {
            List<String> dom = domains.get(slot - 1);
            if (dom.contains(bot)) {
                dom.remove(bot);
                pruned.add(new int[]{slot - 1, Arrays.asList(CSPFramework.BOTS).indexOf(bot)});
                if (dom.isEmpty()) return null; // Domain wipeout = failure
            }
        }

        // Check right neighbor (slot + 1)
        if (slot < CSPFramework.NUM_SLOTS - 1 && assignment[slot + 1] == null) {
            List<String> dom = domains.get(slot + 1);
            if (dom.contains(bot)) {
                dom.remove(bot);
                pruned.add(new int[]{slot + 1, Arrays.asList(CSPFramework.BOTS).indexOf(bot)});
                if (dom.isEmpty()) return null; // Domain wipeout = failure
            }
        }

        return pruned;
    }

    /**
     * Undo Forward Checking by restoring pruned values.
     */
    static void undoForwardCheck(List<int[]> pruned, List<List<String>> domains) {
        for (int[] p : pruned) {
            domains.get(p[0]).add(CSPFramework.BOTS[p[1]]);
        }
    }

    // -------------------- Backtracking Algorithm --------------------

    /**
     * Recursive backtracking with MRV + Forward Checking.
     * @param showSteps if true, prints the first 3 steps in detail
     */
    static boolean backtrack(String[] assignment, List<List<String>> domains,
                             boolean showSteps) {
        // Check if assignment is complete
        boolean complete = true;
        for (String s : assignment) {
            if (s == null) { complete = false; break; }
        }
        if (complete) {
            // Check minimum coverage (global constraint)
            return CSPFramework.checkMinimumCoverage(assignment);
        }

        // Select variable using MRV heuristic
        int slot = selectMRV(assignment, domains);
        if (slot == -1) return false;

        if (showSteps && stepCount < 3) {
            stepCount++;
            System.out.println("--- Step " + stepCount + " ---");
            System.out.println("  MRV selects: " + CSPFramework.SLOT_NAMES[slot]
                    + " (domain size = " + domains.get(slot).size()
                    + ", values = " + domains.get(slot) + ")");
            System.out.print("  Current assignment: {");
            for (int i = 0; i < CSPFramework.NUM_SLOTS; i++) {
                System.out.print(CSPFramework.SLOT_NAMES[i] + "="
                        + (assignment[i] != null ? assignment[i] : "_"));
                if (i < CSPFramework.NUM_SLOTS - 1) System.out.print(", ");
            }
            System.out.println("}");
        }

        // Try each value in the domain
        List<String> domainCopy = new ArrayList<>(domains.get(slot));
        for (String bot : domainCopy) {
            // Check consistency
            if (CSPFramework.isConsistent(assignment, slot, bot)) {
                totalAssignments++;
                assignment[slot] = bot;

                if (showSteps && stepCount <= 3) {
                    System.out.println("  Try: " + CSPFramework.SLOT_NAMES[slot]
                            + " = " + bot);
                }

                // Deep copy domains for forward checking
                List<List<String>> domainsCopy = new ArrayList<>();
                for (List<String> d : domains) domainsCopy.add(new ArrayList<>(d));

                // Forward checking
                List<int[]> pruned = forwardCheck(assignment, domains, slot, bot);

                if (pruned != null) {
                    // Check if coverage can still be satisfied
                    if (CSPFramework.canSatisfyCoverage(assignment, domains)) {
                        if (showSteps && stepCount <= 3) {
                            System.out.println("  Forward Check: domains after pruning:");
                            for (int i = 0; i < CSPFramework.NUM_SLOTS; i++) {
                                if (assignment[i] == null) {
                                    System.out.println("    " + CSPFramework.SLOT_NAMES[i]
                                            + " = " + domains.get(i));
                                }
                            }
                        }

                        if (backtrack(assignment, domains, showSteps)) {
                            return true;
                        }
                    }
                    undoForwardCheck(pruned, domains);
                } else {
                    if (showSteps && stepCount <= 3) {
                        System.out.println("  Forward Check: DOMAIN WIPEOUT! Backtracking.");
                    }
                    // Restore domains from copy
                    for (int i = 0; i < domains.size(); i++) {
                        domains.set(i, domainsCopy.get(i));
                    }
                }

                // Undo assignment
                assignment[slot] = null;
            }
        }

        return false; // No valid assignment found for this branch
    }

    // -------------------- Main --------------------

    public static void main(String[] args) throws Exception {
        String inputFile = args.length > 0 ? args[0] : "input.txt";
        CSPFramework.readInput(inputFile);

        System.out.println("#".repeat(55));
        System.out.println("# SECURITY BOT SCHEDULING - CSP");
        System.out.println("# Backtracking + MRV + Forward Checking");
        System.out.println("#".repeat(55));
        System.out.println();

        // Print constraint graph
        CSPFramework.printConstraintGraph();

        // Initialize domains
        List<List<String>> domains = CSPFramework.initDomains();
        System.out.println("Initial Domains:");
        for (int i = 0; i < CSPFramework.NUM_SLOTS; i++) {
            System.out.println("  " + CSPFramework.SLOT_NAMES[i] + " = " + domains.get(i));
        }
        System.out.println();

        // Show first 3 steps of backtracking with MRV
        System.out.println("=== First 3 Steps of Backtracking with MRV ===\n");

        String[] assignment = new String[CSPFramework.NUM_SLOTS];
        totalAssignments = 0;
        stepCount = 0;
        long startTime = System.currentTimeMillis();

        boolean success = backtrack(assignment, domains, true);

        long timeMs = System.currentTimeMillis() - startTime;

        System.out.println();
        CSPFramework.printResult(
                "Backtracking Search",
                "MRV (Minimum Remaining Values)",
                "Forward Checking",
                success, assignment, totalAssignments, timeMs);
    }
}
