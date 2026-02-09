# Security Bot Scheduling — Constraint Satisfaction Problem

**Student:** Suresh Babu Gandla  
**Roll No:** 123245  
**Email:** Suresh@gmail.com  
**Institute:** Indian Institute of Technology Jodhpur (M.Tech)

## Problem Description

A digital library runs 24/7 and requires assigning **3 Security Bots {A, B, C}** to **4 Time Slots {1, 2, 3, 4}**.

### Variables
`{Slot1, Slot2, Slot3, Slot4}` — each slot must be assigned one bot.

### Domain
`{A, B, C}` — the available bots for each slot.

### Constraints
| # | Constraint | Description |
|---|-----------|-------------|
| 1 | **No Back-to-Back** | A bot cannot work two consecutive slots: `Slot[i] ≠ Slot[i+1]` |
| 2 | **Maintenance Break** | Bot C cannot work in Slot 4 (undergoing updates) |
| 3 | **Minimum Coverage** | Every bot {A, B, C} must be used at least once |

### Constraint Graph
```
[Slot1] ------- [Slot2] ------- [Slot3] ------- [Slot4]
{A,B,C}         {A,B,C}         {A,B,C}         {A,B}
                                              (C excluded)
```
Edges represent the "No Back-to-Back" binary constraint between consecutive slots.

## Implemented Algorithms

| File | Algorithm | Description |
|------|-----------|-------------|
| `BacktrackingSearch.java` | Backtracking + MRV + FC | Main solver with MRV heuristic and Forward Checking |
| `ArcConsistency.java` | AC-3 + Backtracking | Arc Consistency preprocessing followed by backtracking |
| `ForwardCheckingDemo.java` | Forward Checking Demo | Step-by-step demonstration of failure detection |
| `CSPFramework.java` | Shared Framework | Domain init, constraint checking, display utilities |
| `SecurityBotCSP.java` | Master Runner | Runs all algorithms sequentially |

## Project Structure

```
security-bot-csp/
├── README.md
├── input.txt
└── src/
    ├── CSPFramework.java          # Shared utilities
    ├── SecurityBotCSP.java        # Master runner
    ├── BacktrackingSearch.java    # Backtracking + MRV + Forward Checking
    ├── ArcConsistency.java        # AC-3 Arc Consistency
    └── ForwardCheckingDemo.java   # Forward Checking demonstration
```

## How to Compile and Run

### Prerequisites
- Java 17 or higher

### Compile
```bash
cd src
javac *.java
```

### Run all
```bash
java SecurityBotCSP ../input.txt
```

### Run individually
```bash
java BacktrackingSearch ../input.txt
java ArcConsistency ../input.txt
java ForwardCheckingDemo ../input.txt
```

## Input Format (`input2.txt`)
```
# Bots (Domain)
BOTS: A, B, C
# Time Slots (Variables)
SLOTS: 1, 2, 3, 4
# Unary Constraints
BOT_C_NOT_IN: 4
```

## Output Format
Each algorithm prints:
- **Status**: SUCCESS / FAILURE
- **Heuristic**: MRV
- **Inference**: Forward Checking / AC-3
- **Constraints Applied**: All three constraints listed
- **Final Assignment**: Table mapping slots to bots
- **Total Assignments**: Number of variable assignments attempted
- **Time Taken**: Execution time in milliseconds
- **Constraint Verification**: Each constraint checked against solution

## Sample Solution
```
+--------+-----+
| Slot   | Bot |
+--------+-----+
| Slot 1 |  A  |
| Slot 2 |  B  |
| Slot 3 |  C  |
| Slot 4 |  A  |
+--------+-----+
```
