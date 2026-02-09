import java.util.*;
import java.io.*;

/**
 * CSPFramework - Shared utility class for the Security Bot Scheduling CSP.
 *
 * Problem:
 *   Variables : {Slot1, Slot2, Slot3, Slot4}
 *   Domains   : Each slot can be assigned {A, B, C}
 *   Constraints:
 *     1. No Back-to-Back: A bot cannot work two consecutive slots
 *     2. Maintenance Break: Bot C cannot work in Slot 4
 *     3. Minimum Coverage: Every bot (A, B, C) must be used at least once
 *
 * Student : Suresh Babu Gandla | Roll No : 123245
 * IIT Jodhpur - M.Tech Programme
 */
public class CSPFramework {

    // Problem constants
    public static final String[] BOTS = {"A", "B", "C"};
    public static final int NUM_SLOTS = 4;
    public static final String[] SLOT_NAMES = {"Slot1", "Slot2", "Slot3", "Slot4"};

    // -------------------- Input Parsing --------------------

    /**
     * Read CSP configuration from input.txt.
     * Returns a map with parsed values.
     */
    public static Map<String, Object> readInput(String filename) throws Exception {
        Map<String, Object> config = new HashMap<>();
        List<String> bots = new ArrayList<>();
        List<Integer> slots = new ArrayList<>();
        Map<String, Set<Integer>> unaryExclusions = new HashMap<>();

        Scanner sc = new Scanner(new File(filename));
        while (sc.hasNextLine()) {
            String line = sc.nextLine().trim();
            if (line.isEmpty() || line.startsWith("#")) continue;

            if (line.startsWith("BOTS:")) {
                String[] parts = line.substring(5).trim().split(",");
                for (String p : parts) bots.add(p.trim());
            } else if (line.startsWith("SLOTS:")) {
                String[] parts = line.substring(6).trim().split(",");
                for (String p : parts) slots.add(Integer.parseInt(p.trim()));
            } else if (line.startsWith("BOT_C_NOT_IN:")) {
                String[] parts = line.substring(13).trim().split(",");
                Set<Integer> excluded = new HashSet<>();
                for (String p : parts) excluded.add(Integer.parseInt(p.trim()));
                unaryExclusions.put("C", excluded);
            }
        }
        sc.close();

        config.put("bots", bots.isEmpty() ? Arrays.asList(BOTS) : bots);
        config.put("slots", slots.isEmpty() ? Arrays.asList(1, 2, 3, 4) : slots);
        config.put("unaryExclusions", unaryExclusions);
        return config;
    }

    // -------------------- Domain Initialization --------------------

    /**
     * Initialize domains for each slot variable.
     * Applies unary constraint: Bot C cannot be in Slot 4.
     */
    public static List<List<String>> initDomains() {
        List<List<String>> domains = new ArrayList<>();
        for (int i = 0; i < NUM_SLOTS; i++) {
            List<String> domain = new ArrayList<>(Arrays.asList(BOTS));
            // Unary constraint: Bot C cannot work in Slot 4 (index 3)
            if (i == 3) {
                domain.remove("C");
            }
            domains.add(domain);
        }
        return domains;
    }

    // -------------------- Constraint Checking --------------------

    /**
     * Check No Back-to-Back constraint:
     * Assignment[slot] != Assignment[slot+1] for consecutive slots.
     */
    public static boolean checkNoBackToBack(String[] assignment) {
        for (int i = 0; i < assignment.length - 1; i++) {
            if (assignment[i] != null && assignment[i + 1] != null
                    && assignment[i].equals(assignment[i + 1])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check Maintenance Break constraint:
     * Bot C cannot be assigned to Slot 4 (index 3).
     */
    public static boolean checkMaintenanceBreak(String[] assignment) {
        return assignment[3] == null || !assignment[3].equals("C");
    }

    /**
     * Check Minimum Coverage constraint:
     * Every bot {A, B, C} must appear at least once.
     * Only checked when assignment is complete.
     */
    public static boolean checkMinimumCoverage(String[] assignment) {
        Set<String> used = new HashSet<>();
        for (String bot : assignment) {
            if (bot != null) used.add(bot);
        }
        return used.containsAll(Arrays.asList(BOTS));
    }

    /**
     * Check if a partial assignment is consistent (does not violate any constraint).
     * For partial assignments, only checks constraints that can be evaluated.
     */
    public static boolean isConsistent(String[] assignment, int slot, String bot) {
        // Temporarily assign
        assignment[slot] = bot;

        // Check No Back-to-Back with neighbors
        if (slot > 0 && assignment[slot - 1] != null
                && assignment[slot - 1].equals(bot)) {
            assignment[slot] = null;
            return false;
        }
        if (slot < NUM_SLOTS - 1 && assignment[slot + 1] != null
                && assignment[slot + 1].equals(bot)) {
            assignment[slot] = null;
            return false;
        }

        // Check Maintenance Break
        if (slot == 3 && bot.equals("C")) {
            assignment[slot] = null;
            return false;
        }

        // Undo temporary assignment
        assignment[slot] = null;
        return true;
    }

    /**
     * Check if minimum coverage CAN still be satisfied given current assignment.
     * Returns false if any bot has no remaining slot it could be assigned to.
     */
    public static boolean canSatisfyCoverage(String[] assignment, List<List<String>> domains) {
        Set<String> assigned = new HashSet<>();
        int unassigned = 0;
        for (int i = 0; i < NUM_SLOTS; i++) {
            if (assignment[i] != null) {
                assigned.add(assignment[i]);
            } else {
                unassigned++;
            }
        }

        // Check if each unassigned bot can still be placed
        for (String bot : BOTS) {
            if (!assigned.contains(bot)) {
                boolean canPlace = false;
                for (int i = 0; i < NUM_SLOTS; i++) {
                    if (assignment[i] == null && domains.get(i).contains(bot)) {
                        canPlace = true;
                        break;
                    }
                }
                if (!canPlace) return false;
            }
        }
        return true;
    }

    // -------------------- Display --------------------

    /**
     * Print the assignment in the required output format.
     */
    public static void printAssignment(String[] assignment) {
        System.out.println("\nFinal Assignment:");
        System.out.println("+--------+-----+");
        System.out.println("| Slot   | Bot |");
        System.out.println("+--------+-----+");
        for (int i = 0; i < NUM_SLOTS; i++) {
            System.out.printf("| Slot %d |  %s  |%n", i + 1,
                    assignment[i] != null ? assignment[i] : "?");
        }
        System.out.println("+--------+-----+");
    }

    /**
     * Print complete result summary.
     */
    public static void printResult(String algorithm, String heuristic,
                                    String inference, boolean success,
                                    String[] assignment, int totalAssignments,
                                    long timeMs) {
        System.out.println("=".repeat(55));
        System.out.println("Algorithm : " + algorithm);
        System.out.println("Heuristic : " + heuristic);
        System.out.println("Inference : " + inference);
        System.out.println("=".repeat(55));
        System.out.println("Status           : " + (success ? "SUCCESS" : "FAILURE"));
        System.out.println("Total Assignments: " + totalAssignments);
        System.out.println("Time Taken       : " + timeMs + " ms");

        System.out.println("\nConstraints Applied:");
        System.out.println("  1. No Back-to-Back  : Slot[i] != Slot[i+1]");
        System.out.println("  2. Maintenance Break: Bot C not in Slot 4");
        System.out.println("  3. Minimum Coverage : All bots {A,B,C} used");

        if (success && assignment != null) {
            printAssignment(assignment);

            // Verify constraints
            System.out.println("\nConstraint Verification:");
            System.out.println("  No Back-to-Back  : "
                    + (checkNoBackToBack(assignment) ? "SATISFIED" : "VIOLATED"));
            System.out.println("  Maintenance Break : "
                    + (checkMaintenanceBreak(assignment) ? "SATISFIED" : "VIOLATED"));
            System.out.println("  Minimum Coverage  : "
                    + (checkMinimumCoverage(assignment) ? "SATISFIED" : "VIOLATED"));
        }
        System.out.println();
    }

    /**
     * Print constraint graph (adjacency representation).
     */
    public static void printConstraintGraph() {
        System.out.println("Constraint Graph (No Back-to-Back edges):");
        System.out.println("  Nodes: Slot1, Slot2, Slot3, Slot4");
        System.out.println("  Edges (binary constraints):");
        System.out.println("    Slot1 --- Slot2  (Slot1 != Slot2)");
        System.out.println("    Slot2 --- Slot3  (Slot2 != Slot3)");
        System.out.println("    Slot3 --- Slot4  (Slot3 != Slot4)");
        System.out.println();
        System.out.println("  Visual:");
        System.out.println("    [Slot1] ------- [Slot2] ------- [Slot3] ------- [Slot4]");
        System.out.println("    {A,B,C}         {A,B,C}         {A,B,C}         {A,B}");
        System.out.println("                                                  (C excluded)");
        System.out.println();
    }
}
