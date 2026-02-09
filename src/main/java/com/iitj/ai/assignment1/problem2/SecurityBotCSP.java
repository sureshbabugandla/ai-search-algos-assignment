/**
 * SecurityBotCSP - Master runner for all CSP algorithms.
 * Runs Backtracking+MRV+FC, AC-3, and Forward Checking demo.
 *
 * Usage: javac *.java && java SecurityBotCSP [input.txt]
 *
 * Student : Suresh Babu Gandla | Roll No : 123245
 * IIT Jodhpur - M.Tech Programme
 */
public class SecurityBotCSP {

    public static void main(String[] args) throws Exception {
        String inputFile = args.length > 0 ? args[0] : "input.txt";

        System.out.println("#".repeat(55));
        System.out.println("#  SECURITY BOT SCHEDULING - COMPLETE ANALYSIS");
        System.out.println("#  Student: Suresh Babu Gandla (123245)");
        System.out.println("#  IIT Jodhpur - M.Tech Programme");
        System.out.println("#".repeat(55));
        System.out.println();

        // Section 1: Backtracking + MRV + Forward Checking
        System.out.println("*".repeat(55));
        System.out.println("*  SECTION 2: Backtracking + MRV + Forward Checking");
        System.out.println("*".repeat(55));
        System.out.println();
        BacktrackingSearch.main(new String[]{inputFile});

        // Section 2: AC-3 Arc Consistency
        System.out.println("*".repeat(55));
        System.out.println("*  AC-3 ARC CONSISTENCY");
        System.out.println("*".repeat(55));
        System.out.println();
        ArcConsistency.main(new String[]{inputFile});

        // Section 3: Forward Checking Demo
        System.out.println("*".repeat(55));
        System.out.println("*  SECTION 3: Forward Checking Demonstration");
        System.out.println("*".repeat(55));
        System.out.println();
        ForwardCheckingDemo.main(new String[]{inputFile});
    }
}
