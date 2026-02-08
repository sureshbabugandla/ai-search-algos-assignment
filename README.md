# Manuscript Sorting Problem — Search Strategies

**Student:** Suresh Babu Gandla  
**Roll No:** 123245  
**Email:** Suresh@gmail.com  
**Institute:** Indian Institute of Technology Jodhpur (M.Tech)

## Problem Description

A variation of the classic **8-Puzzle** problem. A 3×3 grid contains 8 uniquely numbered manuscripts (1–8) and one empty slot (`B` for Blank). The objective is to rearrange manuscripts from a scrambled initial state to the goal state by sliding manuscripts into the adjacent empty slot.

```
Initial State:          Goal State:
1 2 3                   1 2 3
B 4 6        →          4 5 6
7 5 8                   7 8 B
```

Each move costs **1 unit of System Energy**. The goal is to find an optimal (or near-optimal) sequence of moves.

## Implemented Algorithms

| # | Algorithm | File | Type | Optimal? |
|---|-----------|------|------|----------|
| 1 | Breadth-First Search (BFS) | `BFSSearch.java` | Uninformed | ✅ Yes |
| 2 | Depth-First Search (DFS) | `DFSSearch.java` | Uninformed | ❌ No |
| 3 | Greedy Best-First Search | `GreedyBestFirstSearch.java` | Informed | ❌ No |
| 4 | A* Search (h₁ & h₂) | `AStarSearch.java` | Informed | ✅ Yes |
| 5 | Iterative Deepening A* (IDA*) | `IDAStarSearch.java` | Memory-Bounded | ✅ Yes |
| 6 | Simulated Annealing | `SimulatedAnnealingSearch.java` | Local Search | ❌ No |
| 7 | Minimax | `AdversarialSearch.java` | Adversarial | ✅ (minimax-optimal) |
| 8 | Alpha-Beta Pruning | `AdversarialSearch.java` | Adversarial | ✅ (same as Minimax) |

### Shared Files

| File | Description |
|------|-------------|
| `PuzzleState.java` | Common utility class: state representation, neighbor generation, heuristics (h₁, h₂), path reconstruction, result printing |
| `ManuscriptSorter.java` | Master runner that executes all algorithms sequentially |
| `input.txt` | Input file with start and goal states |

## Heuristics

- **h₁ — Misplaced Tiles:** Count of manuscripts not in their goal position (admissible)
- **h₂ — Manhattan Distance:** Sum of horizontal + vertical distances of each tile from its goal position (admissible, dominates h₁)

## Project Structure

```
manuscript-sorting/
├── README.md
├── input.txt                        # Start: 123;B46;758  Goal: 123 456 78B
└── src/
    ├── PuzzleState.java             # Shared utilities
    ├── ManuscriptSorter.java        # Master runner
    ├── BFSSearch.java               # Breadth-First Search
    ├── DFSSearch.java               # Depth-First Search
    ├── GreedyBestFirstSearch.java   # Greedy Best-First Search
    ├── AStarSearch.java             # A* Search (h1 & h2)
    ├── IDAStarSearch.java           # Iterative Deepening A*
    ├── SimulatedAnnealingSearch.java # Simulated Annealing
    └── AdversarialSearch.java       # Minimax & Alpha-Beta Pruning
```

## How to Compile and Run

### Prerequisites
- Java 17 or higher (`java --version` to check)

### Compile all files
```bash
cd src
javac *.java
```

### Run all algorithms at once
```bash
java ManuscriptSorter ../input.txt
```

### Run individual algorithms
```bash
java BFSSearch ../input.txt
java DFSSearch ../input.txt
java GreedyBestFirstSearch ../input.txt
java AStarSearch ../input.txt
java IDAStarSearch ../input.txt
java SimulatedAnnealingSearch ../input.txt
java AdversarialSearch ../input.txt
```

## Input Format

File `input.txt` contains two lines:
```
123;B46;758       ← Start state (semicolons separate rows, B = blank)
123 456 78B       ← Goal state (spaces separate rows)
```

## Output Format

Each algorithm prints:
- **Status:** SUCCESS / FAILURE
- **States Explored:** Total number of states expanded
- **Time Taken:** Execution time in milliseconds
- **Path Length:** Number of moves in the solution
- **Path:** Sequence of moves (e.g., `Down -> Right -> Down`)
- **Step-by-step:** Visual grid at each step

## Sample Output (BFS)

```
============================================================
Algorithm    : Breadth-First Search (BFS)
============================================================
Status       : SUCCESS
States Explored: 17
Time Taken   : 1 ms
Path Length  : 3 moves
Path         : Down -> Right -> Down

Step-by-step:
Initial State:
1 2 3
B 4 6
7 5 8
  | Move 1: Down
  v
1 2 3
4 B 6
7 5 8       (moved manuscript 4 down into blank)
  | Move 2: Right (moved manuscript 5 right)  (Blank moves right, tile 6... wait)
  ...
```

## Key Design Decisions

1. **Cycle Prevention:** All algorithms use visited sets (or path-based detection for IDA*) to prevent revisiting states.
2. **DFS Depth Limit:** Set to 50 (above the maximum optimal depth of 31 for any 8-puzzle) to prevent infinite paths.
3. **Simulated Annealing Cooling:** Geometric schedule with α=0.9995, balancing exploration and convergence.
4. **Adversarial Utility:** `u(s) = -h₂(s)` — natural formulation where MAX minimizes distance to goal.

## License

This project is submitted as an academic assignment for the AI course at IIT Jodhpur.
