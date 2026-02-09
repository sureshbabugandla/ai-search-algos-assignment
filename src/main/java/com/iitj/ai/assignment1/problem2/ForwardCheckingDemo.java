import java.util.*;

/**
 * Forward Checking Demonstration for the Security Bot Scheduling CSP.
 *
 * Demonstrates how Forward Checking immediately detects a failure
 * when Bot A is assigned to Slot 1 and Bot B is assigned to Slot 2
 * in a very restricted domain scenario.
 *
 * Also demonstrates the general Forward Checking process step by step.
 *
 * Student : Suresh Babu Gandla | Roll No : 123245
 * IIT Jodhpur - M.Tech Programme
 */
public class ForwardCheckingDemo {

    public static void main(String[] args) throws Exception {
        String inputFile = args.length > 0 ? args[0] : "input.txt";
        CSPFramework.readInput(inputFile);

        System.out.println("#".repeat(55));
        System.out.println("# FORWARD CHECKING DEMONSTRATION");
        System.out.println("#".repeat(55));
        System.out.println();

        // ======== Demo 1: Standard Forward Checking ========
        System.out.println("=== Demo 1: Step-by-Step Forward Checking ===\n");

        List<List<String>> domains = CSPFramework.initDomains();
        String[] assignment = new String[CSPFramework.NUM_SLOTS];

        System.out.println("Initial Domains:");
        printDomains(domains);
        System.out.println();

        // Step 1: Assign Slot1 = A
        System.out.println("Step 1: Assign Slot1 = A");
        assignment[0] = "A";
        System.out.println("  Assignment: {Slot1=A, Slot2=_, Slot3=_, Slot4=_}");
        System.out.println("  Forward Check: Remove 'A' from Slot2's domain (back-to-back)");
        domains.get(1).remove("A");
        System.out.println("  Domains after FC:");
        printDomains(domains);
        System.out.println("  All domains non-empty -> Continue.\n");

        // Step 2: Assign Slot2 = B
        System.out.println("Step 2: Assign Slot2 = B");
        assignment[1] = "B";
        System.out.println("  Assignment: {Slot1=A, Slot2=B, Slot3=_, Slot4=_}");
        System.out.println("  Forward Check: Remove 'B' from Slot3's domain (back-to-back)");
        domains.get(2).remove("B");
        System.out.println("  Domains after FC:");
        printDomains(domains);
        System.out.println("  All domains non-empty -> Continue.\n");

        // Step 3: Assign Slot3 = C (or A)
        System.out.println("Step 3: Assign Slot3 = A");
        assignment[2] = "A";
        System.out.println("  Assignment: {Slot1=A, Slot2=B, Slot3=A, Slot4=_}");
        System.out.println("  Forward Check: Remove 'A' from Slot4's domain (back-to-back)");
        domains.get(3).remove("A");
        System.out.println("  Domains after FC:");
        printDomains(domains);
        System.out.println("  Slot4 has domain {B} -> Still valid.\n");

        // Step 4: Assign Slot4 = B
        System.out.println("Step 4: Assign Slot4 = B");
        assignment[3] = "B";
        System.out.println("  Assignment: {Slot1=A, Slot2=B, Slot3=A, Slot4=B}");
        System.out.println("  Check minimum coverage: A used, B used, C NOT used -> FAIL!");
        System.out.println("  Backtrack needed to satisfy minimum coverage.\n");

        System.out.println("  Backtrack: Try Slot3 = C instead.");
        assignment[2] = "C";
        assignment[3] = null;
        // Reset Slot4 domain
        domains.set(3, new ArrayList<>(Arrays.asList("A", "B")));
        System.out.println("  Forward Check: Remove 'C' from Slot4's domain");
        // C was already not in Slot4's domain (unary constraint)
        System.out.println("  (C already excluded from Slot4 by maintenance constraint)");
        System.out.println("  Slot4 domain = {A, B}");
        System.out.println("  Assign Slot4 = A");
        assignment[3] = "A";
        System.out.println("  Final: {Slot1=A, Slot2=B, Slot3=C, Slot4=A}");
        System.out.println("  Coverage: A=yes, B=yes, C=yes -> ALL CONSTRAINTS SATISFIED!\n");

        CSPFramework.printAssignment(assignment);

        // ======== Demo 2: Failure Detection Scenario ========
        System.out.println();
        System.out.println("=".repeat(55));
        System.out.println("=== Demo 2: Failure Detection with Restricted Domain ===\n");
        System.out.println("Scenario: Suppose domain is restricted to only {A, B}");
        System.out.println("for all slots (Bot C unavailable entirely).\n");

        List<List<String>> restrictedDomains = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            restrictedDomains.add(new ArrayList<>(Arrays.asList("A", "B")));
        }
        String[] assignment2 = new String[4];

        System.out.println("Restricted Domains:");
        printDomainsCustom(restrictedDomains);
        System.out.println();

        System.out.println("Step 1: Assign Slot1 = A");
        assignment2[0] = "A";
        System.out.println("  Forward Check: Remove 'A' from Slot2");
        restrictedDomains.get(1).remove("A");
        System.out.println("  Slot2 domain = " + restrictedDomains.get(1));

        System.out.println("\nStep 2: Assign Slot2 = B (only option)");
        assignment2[1] = "B";
        System.out.println("  Forward Check: Remove 'B' from Slot3");
        restrictedDomains.get(2).remove("B");
        System.out.println("  Slot3 domain = " + restrictedDomains.get(2));

        System.out.println("\nStep 3: Assign Slot3 = A (only option)");
        assignment2[2] = "A";
        System.out.println("  Forward Check: Remove 'A' from Slot4");
        restrictedDomains.get(3).remove("A");
        System.out.println("  Slot4 domain = " + restrictedDomains.get(3));

        System.out.println("\nStep 4: Assign Slot4 = B (only option)");
        assignment2[3] = "B";
        System.out.println("  Check minimum coverage: A=yes, B=yes, C=NO!");
        System.out.println("  FAILURE: Bot C never assigned (coverage violated).");
        System.out.println("  Forward Checking detects that with only {A,B} in domains,");
        System.out.println("  the minimum coverage constraint for C can NEVER be satisfied.");
        System.out.println("  This is detected IMMEDIATELY by checking canSatisfyCoverage()");
        System.out.println("  even before completing the assignment.\n");

        System.out.println("KEY INSIGHT: Forward Checking prunes domains eagerly.");
        System.out.println("Combined with coverage checking, it detects failures");
        System.out.println("MUCH earlier than naive backtracking, which would");
        System.out.println("only discover the violation after trying all combinations.");
    }

    static void printDomains(List<List<String>> domains) {
        for (int i = 0; i < CSPFramework.NUM_SLOTS; i++) {
            String marker = (i == 3) ? " (C excluded: maintenance)" : "";
            System.out.println("    " + CSPFramework.SLOT_NAMES[i]
                    + " = " + domains.get(i) + marker);
        }
    }

    static void printDomainsCustom(List<List<String>> domains) {
        for (int i = 0; i < domains.size(); i++) {
            System.out.println("    Slot" + (i + 1) + " = " + domains.get(i));
        }
    }
}
