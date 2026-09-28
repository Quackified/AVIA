# AlgoLens Architecture & Future Work Roadmap

This document outlines architectural roadmaps, proposed algorithmic families, and extensible design patterns for future iterations of AlgoLens.

---

## 1. Algorithmic Families & Variants

### 1.1 Binary Search Tree (BST) Traversal Family
Currently, AlgoLens supports binary search key lookup and insertion on BSTs. Extending this into a unified BST Traversal family will provide deep visualization of recursive and iterative tree traversal techniques:
- **Pre-Order Traversal ($N \to L \to R$)**: Visualizing tree cloning, prefix notation, and structural serialization.
- **In-Order Traversal ($L \to N \to R$)**: Revealing monotonic sorted output invariant from binary search trees.
- **Post-Order Traversal ($L \to R \to N$)**: Visualizing bottom-up memory deallocation, subtree property checks, and postfix evaluations.
- **Level-Order (Breadth-First) Traversal**: Demonstrating queue-driven horizontal tree slicing and depth instrumentation.

*Implementation Design*:
- Extend `AlgorithmId` with `BST_PREORDER`, `BST_INORDER`, `BST_POSTORDER`, `BST_LEVELORDER` or a sub-mode parameter in `GraphCustomization.ForBst`.
- Telemetry strip: Display the call stack frame depth alongside the accumulated visited output sequence.

### 1.2 Queue & Buffer Family Variants
AlgoLens currently features standard LIFO `Stack` and FIFO `Queue` containers. Expanding the linear buffer family:
- **Circular Queue (Ring Buffer)**: Visualizing modular arithmetic pointer indexing `(head + 1) % capacity`, full/empty condition disambiguation, and cache-friendly buffer streaming.
- **Double-Ended Queue (Deque)**: Supporting $O(1)$ push/pop at both front and back boundaries.
- **Priority Queue / Binary Min-Max Heap**: Interactive step trace illustrating sift-up and sift-down adjustments when priority keys change dynamically.
- **Monotonic Queue / Deque**: Visualizing sliding window minimum/maximum query maintenance in $O(n)$ amortized time.

### 1.3 0-1 BFS (Double-Ended Queue Shortest Path)
For graphs with non-negative binary weights ($w \in \{0, 1\}$), standard Dijkstra with binary heap incurs $O((V+E) \log V)$ overhead, whereas 0-1 BFS solves single-source shortest paths in optimal $O(V+E)$ time using a Deque.
- Edges with weight 0 push the relaxed neighbor to the **front** of the deque (`pushFront`).
- Edges with weight 1 push the relaxed neighbor to the **back** of the deque (`pushBack`).
- Visualizer telemetry strip: Display the active Deque with color-coded 0-cost front extractions and 1-cost back extractions.

### 1.4 A* Heuristic Search ($f(n) = g(n) + h(n)$)
Extending the weighted graph / 2D grid visualizer to support directed heuristic search:
- **Evaluation Function**: $f(n) = g(n) + h(n)$, where $g(n)$ is the exact cost from START to $n$, and $h(n)$ is an admissible heuristic (e.g. Euclidean or Manhattan distance to TARGET).
- **Open and Closed Sets**: Render Open Set nodes with cyan glow (with $f, g, h$ pill badges) and Closed Set with dark visited states.
- Interactive mode: Allow the user to drag the TARGET node in real-time, observing the heuristic field recalculate dynamically.

---

## 2. Interactive Graph Editor Enhancements

### 2.1 Directed Edge Support & Arrowheads
- **Current State**: Graphs default to undirected symmetric edges ($u \leftrightarrow v$).
- **Proposed Enhancement**: A per-edge or global directed toggle (`isDirected`).
- Canvas rendering: Draw directed chevron arrowheads using tangent angles:
  $$\theta = \text{atan2}(y_2 - y_1, x_2 - x_1)$$
  offset by the node circle boundary radius ($R = 17\text{dp}$).
- Traversal implications: Adjacency list populates strictly $u \to v$, enabling visualization of directed acyclic graphs (DAGs), Topological Sort, and Kahn's algorithm.

### 2.2 Multi-Step Undo / Redo History Stack
- Store graph topology snapshots in a bounded circular history buffer:
  `UndoStack: ArrayDeque<Pair<List<GraphNodeState>, List<GraphEdgeState>>>`.
- Keybindings & gestures: Standard Android back action or 2-finger double-tap triggers undo; dedicated undo/redo glyph buttons inside `GraphFloatingToolbar`.

### 2.3 Edge Weight Input Dialog & Custom Negative Weight Warnings
- In addition to the tap-to-cycle weight pill (1..9), provide an inline numeric keypad dialog for arbitrary weights.
- Include invariant guards for Dijkstra: if a negative edge weight is authored, display an informative amber alert:
  *"Dijkstra's algorithm assumes non-negative edge weights ($w \ge 0$). Negative weights can cause infinite relaxation loops or sub-optimal paths; consider Bellman-Ford."*

---

## 3. Challenge Mode & Cognitive Interactivity

### 3.1 Dijkstra Prediction Question Generator
Integrate Dijkstra into `PredictionQuestionBuilder`:
- **Next Min-PQ Extraction**: Ask user *"Which vertex will the Priority Queue extract next with minimum tentative distance?"*
  - Multiple choice options derived from current PQ frontier (`BufferItem`s).
- **Edge Relaxation Outcome**: When examining edge $(u, v)$ with weight $w$, ask:
  *"Does traversing to $v$ via $u$ update $dist[v]$? ($dist[u] + w < dist[v]$)"*
  - Options: *"Yes, relaxes to X"* vs *"No, keep current distance Y"*.
- Direct Canvas Hit: Allow user to tap the predicted node directly on the 2D canvas (leveraging `submitNodePrediction`).
