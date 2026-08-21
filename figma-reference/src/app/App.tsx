import { useState, useEffect } from "react";
import { motion, AnimatePresence } from "motion/react";
import {
  Search, Play, Pause, SkipBack, SkipForward, RotateCcw,
  WifiOff, Bot, GitBranch, ArrowUpDown, Layers, Hash,
  Sparkles, ChevronRight, Zap, TrendingUp, ChevronLeft,
  Code2, Gauge, Database, Trash2, BarChart2, Bookmark,
  Home, Compass, User, Sliders, Settings,
} from "lucide-react";

// ─── Types ───────────────────────────────────────────────────────────────────

interface Algorithm {
  id: number;
  name: string;
  category: string;
  time: string;
  space: string;
  difficulty: "Easy" | "Medium" | "Hard";
  color: string;
}

interface SortStep {
  array: number[];
  type: "compare" | "swap" | "done";
  indices: number[];
  sortedFrom: number;
}

// ─── Static Data ─────────────────────────────────────────────────────────────

const ALGORITHMS: Algorithm[] = [
  { id: 1, name: "Bubble Sort",   category: "Sorting",             time: "O(n²)",      space: "O(1)",     difficulty: "Easy",   color: "#22D3EE" },
  { id: 2, name: "Quick Sort",    category: "Sorting",             time: "O(n log n)", space: "O(log n)", difficulty: "Medium", color: "#22D3EE" },
  { id: 3, name: "Merge Sort",    category: "Sorting",             time: "O(n log n)", space: "O(n)",     difficulty: "Medium", color: "#22D3EE" },
  { id: 4, name: "BFS",           category: "Graphs",              time: "O(V+E)",     space: "O(V)",     difficulty: "Easy",   color: "#4ADE80" },
  { id: 5, name: "Dijkstra's",    category: "Graphs",              time: "O(V²)",      space: "O(V)",     difficulty: "Hard",   color: "#4ADE80" },
  { id: 6, name: "Knapsack 0/1",  category: "Dynamic Programming", time: "O(nW)",      space: "O(nW)",    difficulty: "Hard",   color: "#FB923C" },
  { id: 7, name: "Longest CS",    category: "Dynamic Programming", time: "O(mn)",      space: "O(mn)",    difficulty: "Medium", color: "#FB923C" },
  { id: 8, name: "Binary Search", category: "Searching",           time: "O(log n)",   space: "O(1)",     difficulty: "Easy",   color: "#C084FC" },
];

const CATEGORIES = ["All", "Sorting", "Graphs", "Dynamic Programming", "Searching"] as const;
type Category = typeof CATEGORIES[number];

const BOOKMARKED_ALGORITHMS = ALGORITHMS.filter(a => [2, 5, 6].includes(a.id));
const WEEKLY_ACTIVITY = [3, 5, 2, 7, 4, 6, 3];

function generateBubbleSortSteps(arr: number[]): SortStep[] {
  const steps: SortStep[] = [];
  const a = [...arr];
  const n = a.length;
  for (let i = 0; i < n - 1; i++) {
    for (let j = 0; j < n - 1 - i; j++) {
      steps.push({ array: [...a], type: "compare", indices: [j, j + 1], sortedFrom: n - i });
      if (a[j] > a[j + 1]) {
        [a[j], a[j + 1]] = [a[j + 1], a[j]];
        steps.push({ array: [...a], type: "swap", indices: [j, j + 1], sortedFrom: n - i });
      }
    }
  }
  steps.push({ array: [...a], type: "done", indices: [], sortedFrom: 0 });
  return steps;
}

const INITIAL_ARRAY = [64, 34, 25, 12, 22, 11, 90, 45, 67, 38];
const ALL_STEPS = generateBubbleSortSteps(INITIAL_ARRAY);
const MAX_VAL = 90;
const BAR_H = 150;

function barColor(idx: number, step: SortStep): string {
  if (step.type === "done") return "#22D3EE";
  if (idx >= step.sortedFrom) return "#22D3EE";
  if (step.indices.includes(idx)) return step.type === "swap" ? "#F87171" : "#FBBF24";
  return "#1A2E50";
}

// ─── Shared Primitives ────────────────────────────────────────────────────────

function StatusBar() {
  return (
    <div className="relative flex items-center justify-between px-5 h-8" style={{ fontFamily: "JetBrains Mono, monospace" }}>
      <span style={{ fontSize: 11, fontWeight: 600, color: "#94A3B8" }}>9:41</span>
      <div className="absolute left-1/2 -translate-x-1/2 top-1" style={{ width: 70, height: 20, background: "#060A14", borderRadius: 99 }} />
      <div className="flex items-center gap-1.5" style={{ color: "#94A3B8" }}>
        <svg width="12" height="9" viewBox="0 0 12 9" fill="currentColor">
          <rect x="0" y="5" width="2" height="4" rx="0.5" />
          <rect x="3" y="3.5" width="2" height="5.5" rx="0.5" />
          <rect x="6" y="1.5" width="2" height="7.5" rx="0.5" />
          <rect x="9" y="0" width="2" height="9" rx="0.5" />
        </svg>
        <svg width="15" height="9" viewBox="0 0 15 9" fill="none">
          <rect x="0.5" y="0.5" width="12" height="8" rx="1.5" stroke="currentColor" strokeWidth="1" />
          <rect x="13" y="2.5" width="1.5" height="4" rx="0.5" fill="currentColor" />
          <rect x="1.5" y="1.5" width="9" height="6" rx="0.5" fill="currentColor" />
        </svg>
      </div>
    </div>
  );
}

function AlgoCard({ algo }: { algo: Algorithm }) {
  const Icon =
    algo.category === "Sorting" ? ArrowUpDown :
    algo.category === "Graphs" ? GitBranch :
    algo.category === "Dynamic Programming" ? Layers : Hash;

  const diffStyle =
    algo.difficulty === "Easy"   ? { color: "#4ADE80", background: "rgba(74,222,128,0.1)" } :
    algo.difficulty === "Medium" ? { color: "#FBBF24", background: "rgba(251,191,36,0.1)" } :
                                   { color: "#F87171", background: "rgba(248,113,113,0.1)" };

  return (
    <div
      className="flex items-center gap-3 rounded-xl cursor-pointer"
      style={{ padding: "10px 12px", background: "#0C1526", border: "1px solid rgba(255,255,255,0.05)", fontFamily: "JetBrains Mono, monospace" }}
    >
      <div className="flex items-center justify-center shrink-0" style={{ width: 36, height: 36, borderRadius: 10, background: algo.color + "18" }}>
        <Icon size={14} style={{ color: algo.color }} />
      </div>
      <div className="flex-1 min-w-0">
        <div className="flex items-center justify-between gap-1">
          <span style={{ fontSize: 12, fontWeight: 600, color: "#E2E8F0" }} className="truncate">{algo.name}</span>
          <ChevronRight size={11} style={{ color: "#334155", flexShrink: 0 }} />
        </div>
        <div className="flex items-center gap-2 mt-0.5">
          <span style={{ fontSize: 10, color: "#475569" }}>{algo.time}</span>
          <span style={{ fontSize: 10, color: "#1E3A5F" }}>·</span>
          <span style={{ fontSize: 10, fontWeight: 600, padding: "1px 6px", borderRadius: 5, ...diffStyle }}>{algo.difficulty}</span>
        </div>
      </div>
    </div>
  );
}

function SectionLabel({ icon: Icon, text }: { icon: any; text: string }) {
  return (
    <div className="flex items-center gap-2" style={{ marginBottom: 7 }}>
      <Icon size={11} style={{ color: "#22D3EE" }} />
      <span style={{ fontSize: 9, fontWeight: 700, color: "#334155", letterSpacing: "0.12em", textTransform: "uppercase" }}>{text}</span>
    </div>
  );
}

function ToggleSwitch({ on, onToggle }: { on: boolean; onToggle: () => void }) {
  return (
    <button
      onClick={onToggle}
      style={{
        width: 40, height: 22, borderRadius: 99, border: "none", cursor: "pointer", flexShrink: 0,
        background: on ? "#22D3EE" : "#1A2540",
        padding: 2,
        display: "flex", alignItems: "center",
        justifyContent: on ? "flex-end" : "flex-start",
        transition: "background 0.2s",
      }}
    >
      <motion.div
        layout
        style={{ width: 18, height: 18, borderRadius: 99, background: on ? "#060A14" : "#475569" }}
        transition={{ type: "spring", stiffness: 500, damping: 30 }}
      />
    </button>
  );
}

type NavTab = "home" | "explore" | "profile" | "settings";

function BottomNav({ active }: { active: NavTab }) {
  const tabs: { id: NavTab; icon: any; label: string }[] = [
    { id: "home",     icon: Home,    label: "Home" },
    { id: "explore",  icon: Compass, label: "Explore" },
    { id: "profile",  icon: User,    label: "Profile" },
    { id: "settings", icon: Sliders, label: "Settings" },
  ];

  return (
    <div style={{ display: "flex", alignItems: "center", background: "#080D1B", borderTop: "1px solid rgba(255,255,255,0.06)", padding: "8px 4px 12px", flexShrink: 0 }}>
      {tabs.map(tab => {
        const isActive = active === tab.id;
        return (
          <div key={tab.id} style={{ flex: 1, display: "flex", flexDirection: "column", alignItems: "center", gap: 3, cursor: "pointer" }}>
            <div style={{
              width: 36, height: 30, borderRadius: 9, display: "flex", alignItems: "center", justifyContent: "center",
              background: isActive ? "rgba(34,211,238,0.12)" : "transparent",
            }}>
              <tab.icon size={15} style={{ color: isActive ? "#22D3EE" : "#334155" }} />
            </div>
            <span style={{ fontSize: 8, fontWeight: isActive ? 700 : 400, color: isActive ? "#22D3EE" : "#334155" }}>{tab.label}</span>
          </div>
        );
      })}
    </div>
  );
}

// ─── Screen 01 — Dashboard ────────────────────────────────────────────────────

function DashboardScreen() {
  const [activeCategory, setActiveCategory] = useState<Category>("All");
  const [query, setQuery] = useState("");

  const filtered = ALGORITHMS.filter(a =>
    (activeCategory === "All" || a.category === activeCategory) &&
    (query === "" || a.name.toLowerCase().includes(query.toLowerCase()))
  );

  return (
    <div className="flex flex-col h-full overflow-hidden" style={{ background: "#060A14", fontFamily: "JetBrains Mono, monospace" }}>
      <StatusBar />
      <div style={{ padding: "4px 16px 10px" }}>
        <div className="flex items-center justify-between" style={{ marginBottom: 10 }}>
          <div>
            <div className="flex items-center gap-1.5">
              <Zap size={13} style={{ color: "#22D3EE" }} />
              <span style={{ fontSize: 15, fontWeight: 700, color: "#22D3EE", letterSpacing: "-0.03em" }}>AlgoLens</span>
            </div>
            <span style={{ fontSize: 10, color: "#475569", marginTop: 1, display: "block" }}>8 algorithms available</span>
          </div>
          <div className="flex items-center justify-center" style={{ width: 32, height: 32, borderRadius: 10, background: "rgba(34,211,238,0.1)", border: "1px solid rgba(34,211,238,0.2)" }}>
            <TrendingUp size={13} style={{ color: "#22D3EE" }} />
          </div>
        </div>
        <div className="flex items-center gap-2" style={{ background: "#0C1526", border: "1px solid rgba(255,255,255,0.06)", borderRadius: 12, padding: "8px 12px" }}>
          <Search size={13} style={{ color: "#475569", flexShrink: 0 }} />
          <input
            style={{ flex: 1, background: "transparent", border: "none", outline: "none", fontSize: 12, color: "#CBD5E1", fontFamily: "JetBrains Mono, monospace" }}
            placeholder="Search algorithms..."
            value={query}
            onChange={e => setQuery(e.target.value)}
          />
        </div>
      </div>
      <div style={{ padding: "0 16px 10px" }}>
        <div className="flex gap-1.5 overflow-x-auto" style={{ scrollbarWidth: "none" }}>
          {CATEGORIES.map(cat => {
            const active = activeCategory === cat;
            const label = cat === "Dynamic Programming" ? "DP" : cat;
            return (
              <button key={cat} onClick={() => setActiveCategory(cat)} style={{ flexShrink: 0, fontSize: 10, fontWeight: 600, padding: "5px 10px", borderRadius: 8, border: active ? "none" : "1px solid rgba(255,255,255,0.07)", background: active ? "#22D3EE" : "#0C1526", color: active ? "#060A14" : "#64748B", cursor: "pointer", fontFamily: "JetBrains Mono, monospace", transition: "all 0.15s" }}>
                {label}
              </button>
            );
          })}
        </div>
      </div>
      <div style={{ padding: "0 16px 8px", display: "flex", alignItems: "center", justifyContent: "space-between" }}>
        <span style={{ fontSize: 10, color: "#334155", fontWeight: 600, letterSpacing: "0.1em", textTransform: "uppercase" }}>{filtered.length} result{filtered.length !== 1 ? "s" : ""}</span>
        <span style={{ fontSize: 10, color: "#1E3A5F" }}>Sort: default</span>
      </div>
      <div className="flex-1 overflow-y-auto" style={{ padding: "0 16px 16px", scrollbarWidth: "none", display: "flex", flexDirection: "column", gap: 6 }}>
        {filtered.length > 0 ? filtered.map(a => <AlgoCard key={a.id} algo={a} />) : <p style={{ textAlign: "center", fontSize: 11, color: "#334155", marginTop: 24 }}>No results</p>}
      </div>
      <BottomNav active="home" />
    </div>
  );
}

// ─── Screen 02 — Visualizer ───────────────────────────────────────────────────

interface GraphNode {
  id: string;
  x: number;
  y: number;
  label: string;
}

interface GraphEdge {
  from: string;
  to: string;
  weight: number;
}

const GRAPH_NODES: GraphNode[] = [
  { id: "A", x: 45, y: 35, label: "A" },
  { id: "B", x: 125, y: 25, label: "B" },
  { id: "C", x: 205, y: 40, label: "C" },
  { id: "D", x: 50, y: 110, label: "D" },
  { id: "E", x: 130, y: 120, label: "E" },
  { id: "F", x: 210, y: 105, label: "F" },
];

const GRAPH_EDGES: GraphEdge[] = [
  { from: "A", to: "B", weight: 4 },
  { from: "A", to: "D", weight: 2 },
  { from: "B", to: "C", weight: 5 },
  { from: "B", to: "E", weight: 1 },
  { from: "D", to: "E", weight: 3 },
  { from: "E", to: "C", weight: 2 },
  { from: "E", to: "F", weight: 4 },
  { from: "C", to: "F", weight: 3 },
];

// Dijkstra shortest path from A to F: A -> D -> E -> C -> F (weight 2+3+2+3 = 10) or A -> B -> E -> C -> F (4+1+2+3 = 10) or A -> B -> E -> F (4+1+4 = 9!)
// Shortest path A -> F: A -> B (4) -> E (1) -> F (4) = 9
const DIJKSTRA_PATH_STEPS = [
  { step: 1, activeNode: "A", visited: ["A"], pathEdges: [], description: "Dijkstra: Init source node A (dist=0)" },
  { step: 2, activeNode: "D", visited: ["A", "D"], pathEdges: ["A-D"], description: "Explore neighbor D (weight=2)" },
  { step: 3, activeNode: "B", visited: ["A", "D", "B"], pathEdges: ["A-B"], description: "Explore neighbor B (weight=4)" },
  { step: 4, activeNode: "E", visited: ["A", "D", "B", "E"], pathEdges: ["A-B", "B-E"], description: "Relax edge B→E (total dist=5)" },
  { step: 5, activeNode: "C", visited: ["A", "D", "B", "E", "C"], pathEdges: ["A-B", "B-E", "E-C"], description: "Relax edge E→C (total dist=7)" },
  { step: 6, activeNode: "F", visited: ["A", "D", "B", "E", "C", "F"], pathEdges: ["A-B", "B-E", "E-F"], description: "Shortest path A→F found (dist=9) ✓" },
];

function VisualizerScreen({ onTutorOpen, initialMode = "array" }: { onTutorOpen: () => void; initialMode?: "array" | "graph" }) {
  const [dataStructureMode, setDataStructureMode] = useState<"array" | "graph">(initialMode);
  const [perspectiveView, setPerspectiveView] = useState<"canvas" | "code">("canvas");

  const [inputModalOpen, setInputModalOpen] = useState(false);
  const [traceLang, setTraceLang] = useState<"Kotlin" | "Python" | "Java">("Kotlin");

  // Array state
  const [customArray, setCustomArray] = useState<number[]>([64, 34, 25, 12, 22, 11, 90]);
  const [inputValueStr, setInputValueStr] = useState("64, 34, 25, 12, 22");
  const [arrayLength, setArrayLength] = useState(7);
  const [activePreset, setActivePreset] = useState<string | null>(null);

  const steps = generateBubbleSortSteps(customArray);
  const maxIdx = dataStructureMode === "array" ? steps.length - 1 : DIJKSTRA_PATH_STEPS.length - 1;

  const [stepIdx, setStepIdx] = useState(0);
  const [playing, setPlaying] = useState(false);
  const [hoveredNode, setHoveredNode] = useState<string | null>(null);

  useEffect(() => {
    if (!playing) return;
    if (stepIdx >= maxIdx) { setPlaying(false); return; }
    const t = setTimeout(() => setStepIdx(s => s + 1), 520);
    return () => clearTimeout(t);
  }, [playing, stepIdx, maxIdx]);

  const step = steps[Math.min(stepIdx, steps.length - 1)] || steps[0];
  const currentMaxVal = Math.max(...customArray, 1);

  const stepLabel = dataStructureMode === "array"
    ? (step.type === "compare"
      ? `Compare index ${step.indices[0]} [${step.array[step.indices[0]]}] and index ${step.indices[1]} [${step.array[step.indices[1]]}]`
      : step.type === "swap" ? `Swap ${step.array[step.indices[0]]} ↔ ${step.array[step.indices[1]]}`
      : "Sort complete — array sorted ✓")
    : DIJKSTRA_PATH_STEPS[Math.min(stepIdx, DIJKSTRA_PATH_STEPS.length - 1)].description;

  const activeLineNum = dataStructureMode === "array"
    ? (step.type === "compare" ? 4 : step.type === "swap" ? 5 : 7)
    : (stepIdx + 1);

  const kotlinLines = [
    { num: 1, code: "fun bubbleSort(arr: IntArray) {" },
    { num: 2, code: "  val n = arr.size" },
    { num: 3, code: "  for (i in 0 until n - 1) {" },
    { num: 4, code: "    for (j in 0 until n - i - 1) {" },
    { num: 5, code: "      if (arr[j] > arr[j + 1]) {" },
    { num: 6, code: "        arr.swap(j, j + 1)" },
    { num: 7, code: "      }" },
    { num: 8, code: "    }" },
    { num: 9, code: "  }" },
    { num: 10, code: "}" },
  ];

  const pythonLines = [
    { num: 1, code: "def bubble_sort(arr):" },
    { num: 2, code: "    n = len(arr)" },
    { num: 3, code: "    for i in range(n - 1):" },
    { num: 4, code: "        for j in range(n - i - 1):" },
    { num: 5, code: "            if arr[j] > arr[j + 1]:" },
    { num: 6, code: "                arr[j], arr[j+1] = arr[j+1], arr[j]" },
    { num: 7, code: "    return arr" },
  ];

  const javaLines = [
    { num: 1, code: "public void bubbleSort(int[] arr) {" },
    { num: 2, code: "    int n = arr.length;" },
    { num: 3, code: "    for (int i = 0; i < n - 1; i++) {" },
    { num: 4, code: "        for (int j = 0; j < n - i - 1; j++) {" },
    { num: 5, code: "            if (arr[j] > arr[j + 1]) {" },
    { num: 6, code: "                int temp = arr[j];" },
    { num: 7, code: "                arr[j] = arr[j + 1];" },
    { num: 8, code: "                arr[j + 1] = temp;" },
    { num: 9, code: "            }" },
    { num: 10, code: "        }" },
    { num: 11, code: "    }" },
    { num: 12, code: "}" },
  ];

  const currentCodeLines = traceLang === "Kotlin" ? kotlinLines : traceLang === "Python" ? pythonLines : javaLines;

  // Call stack frames
  const callStackFrames = dataStructureMode === "array" ? [
    { id: 1, name: "bubbleSort(arr, n=7)", depth: 0, status: "active", params: `i=${Math.floor(stepIdx / 5)}, j=${step.indices[0] ?? 0}` },
    { id: 2, name: "compareAndSwap(j, j+1)", depth: 1, status: "parent", params: `swapNeeded=${step.type === "swap"}` },
  ] : [
    { id: 1, name: "dijkstra(graph, source='A')", depth: 0, status: "active", params: `visited=${DIJKSTRA_PATH_STEPS[Math.min(stepIdx, DIJKSTRA_PATH_STEPS.length - 1)].visited.length}` },
    { id: 2, name: "extractMin(priorityQueue)", depth: 1, status: "parent", params: `active='${DIJKSTRA_PATH_STEPS[Math.min(stepIdx, DIJKSTRA_PATH_STEPS.length - 1)].activeNode}'` },
  ];

  const applyPreset = (preset: string) => {
    setActivePreset(preset);
    let arr: number[] = [];
    const len = arrayLength;
    if (preset === "Random") {
      arr = Array.from({ length: len }, () => Math.floor(Math.random() * 80) + 10);
    } else if (preset === "Sorted") {
      arr = Array.from({ length: len }, (_, i) => Math.floor(10 + (i * 75) / len));
    } else if (preset === "Reverse Sorted") {
      arr = Array.from({ length: len }, (_, i) => Math.floor(85 - (i * 75) / len));
    } else if (preset === "Nearly Sorted") {
      arr = Array.from({ length: len }, (_, i) => Math.floor(10 + (i * 75) / len));
      if (arr.length > 2) {
        const tmp = arr[1];
        arr[1] = arr[2];
        arr[2] = tmp;
      }
    }
    setCustomArray(arr);
    setInputValueStr(arr.join(", "));
  };

  const handleApplyInput = () => {
    const parsed = inputValueStr.split(",").map(v => parseInt(v.trim(), 10)).filter(v => !isNaN(v) && v > 0);
    if (parsed.length > 0) {
      setCustomArray(parsed);
      setArrayLength(parsed.length);
    }
    setStepIdx(0);
    setPlaying(false);
    setInputModalOpen(false);
  };

  const currentGraphStep = DIJKSTRA_PATH_STEPS[Math.min(stepIdx, DIJKSTRA_PATH_STEPS.length - 1)];

  return (
    <div className="relative flex flex-col h-full overflow-hidden" style={{ background: "#060A14", fontFamily: "JetBrains Mono, monospace" }}>
      <StatusBar />

      {/* 1. TOP CONTROL BAR HEADER */}
      <div className="flex flex-col gap-1.5 px-3.5 pt-3 pb-2" style={{ borderBottom: "1px solid rgba(255,255,255,0.04)" }}>
        {/* Top Line: Back Arrow + Algorithm Name + Offline Badge */}
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-1.5 min-w-0">
            <div style={{ width: 22, height: 22, borderRadius: 6, background: "#0C1526", border: "1px solid rgba(255,255,255,0.08)", display: "flex", alignItems: "center", justifyContent: "center", cursor: "pointer", flexShrink: 0 }}>
              <ChevronLeft size={12} style={{ color: "#94A3B8" }} />
            </div>
            <div className="min-w-0">
              <span className="truncate block" style={{ fontSize: 12, fontWeight: 700, color: "#E2E8F0" }}>
                {dataStructureMode === "array" ? "Bubble Sort" : "Dijkstra's Pathfinding"}
              </span>
              <span style={{ fontSize: 8, color: "#475569" }}>Step {stepIdx + 1} of {maxIdx + 1}</span>
            </div>
          </div>

          <div className="flex items-center gap-1 shrink-0" style={{ background: "rgba(251,146,60,0.12)", border: "1px solid rgba(251,146,60,0.3)", borderRadius: 99, padding: "2px 6px" }}>
            <WifiOff size={8} style={{ color: "#FB923C" }} />
            <span style={{ fontSize: 8, fontWeight: 700, color: "#FB923C" }}>Offline</span>
          </div>
        </div>

        {/* Second Line: Universal View Perspective Toggle Pill */}
        <div style={{ display: "flex", background: "#080D1B", border: "1px solid rgba(255,255,255,0.08)", borderRadius: 8, padding: 1.5, gap: 2 }}>
          <button
            onClick={() => setPerspectiveView("canvas")}
            style={{
              flex: 1, padding: "3px 0", borderRadius: 6, border: "none", fontSize: 8.5, fontWeight: 700, cursor: "pointer",
              background: perspectiveView === "canvas" ? "#22D3EE" : "transparent",
              color: perspectiveView === "canvas" ? "#060A14" : "#64748B",
              transition: "all 0.2s", fontFamily: "JetBrains Mono, monospace"
            }}
          >
            Visual Canvas
          </button>
          <button
            onClick={() => setPerspectiveView("code")}
            style={{
              flex: 1, padding: "3px 0", borderRadius: 6, border: "none", fontSize: 8.5, fontWeight: 700, cursor: "pointer",
              background: perspectiveView === "code" ? "#8B5CF6" : "transparent",
              color: perspectiveView === "code" ? "#FFFFFF" : "#64748B",
              transition: "all 0.2s", fontFamily: "JetBrains Mono, monospace"
            }}
          >
            Code & Stack
          </button>
        </div>
      </div>

      {/* 2. VIEW STATE A: "Visual Canvas" Selected */}
      {perspectiveView === "canvas" ? (
        <div className="flex flex-col flex-1 min-h-0 overflow-hidden" style={{ padding: "6px 14px 0" }}>
          {/* Sub-bar: DS mode switcher + Edit Input */}
          <div className="flex items-center justify-between mb-1.5">
            <div style={{ display: "flex", gap: 4 }}>
              <button
                onClick={() => { setDataStructureMode("array"); setStepIdx(0); setPlaying(false); }}
                style={{
                  padding: "2px 6px", borderRadius: 6, fontSize: 8, fontWeight: 700, cursor: "pointer",
                  background: dataStructureMode === "array" ? "rgba(34,211,238,0.2)" : "#0C1526",
                  color: dataStructureMode === "array" ? "#22D3EE" : "#475569",
                  border: dataStructureMode === "array" ? "1px solid #22D3EE" : "1px solid rgba(255,255,255,0.05)"
                }}
              >
                1D Array
              </button>
              <button
                onClick={() => { setDataStructureMode("graph"); setStepIdx(0); setPlaying(false); }}
                style={{
                  padding: "2px 6px", borderRadius: 6, fontSize: 8, fontWeight: 700, cursor: "pointer",
                  background: dataStructureMode === "graph" ? "rgba(74,222,128,0.2)" : "#0C1526",
                  color: dataStructureMode === "graph" ? "#4ADE80" : "#475569",
                  border: dataStructureMode === "graph" ? "1px solid #4ADE80" : "1px solid rgba(255,255,255,0.05)"
                }}
              >
                2D Graph
              </button>
            </div>

            <button
              onClick={() => setInputModalOpen(true)}
              style={{
                display: "flex", alignItems: "center", gap: 3, padding: "2px 7px", borderRadius: 6,
                background: "rgba(34,211,238,0.08)", border: "1px solid rgba(34,211,238,0.22)",
                color: "#22D3EE", fontSize: 8.5, fontWeight: 600, cursor: "pointer", fontFamily: "JetBrains Mono, monospace"
              }}
            >
              <Sliders size={9} />
              <span>Edit Input</span>
            </button>
          </div>

          {/* Live Step Description Banner */}
          <div style={{ background: "#0C1526", border: "1px solid rgba(255,255,255,0.05)", borderRadius: 8, padding: "5px 9px", marginBottom: 6 }}>
            <p style={{ fontSize: 9, color: "#94A3B8", lineHeight: 1.4, margin: 0 }}>{stepLabel}</p>
          </div>

          {/* Main Visualizer Area */}
          {dataStructureMode === "array" ? (
            /* Array Bar Canvas - FIXED HEIGHT CALCULATION PREVENTING OVERFLOW */
            <div style={{ flex: 1, background: "#080D1B", border: "1px solid rgba(255,255,255,0.04)", borderRadius: 12, padding: "8px 10px 4px", display: "flex", alignItems: "flex-end", gap: 3, minHeight: 0 }}>
              {step.array.map((val, i) => (
                <div key={i} style={{ flex: 1, display: "flex", flexDirection: "column", alignItems: "center", height: "100%", justifyContent: "flex-end" }}>
                  <div style={{ flex: 1, width: "100%", display: "flex", alignItems: "flex-end" }}>
                    <motion.div
                      style={{ width: "100%", borderRadius: "3px 3px 0 0", backgroundColor: barColor(i, step) }}
                      animate={{ height: `${Math.max(6, Math.min(100, (val / currentMaxVal) * 92))}%` }}
                      transition={{ duration: 0.18, ease: "easeOut" }}
                    />
                  </div>
                  <span style={{ fontSize: 8, color: "#64748B", marginTop: 2, fontWeight: 700, flexShrink: 0 }}>{val}</span>
                </div>
              ))}
            </div>
          ) : (
            /* Graph Canvas */
            <div style={{ flex: 1, background: "#080D1B", border: "1px solid rgba(74,222,128,0.12)", borderRadius: 12, padding: 4, position: "relative", minHeight: 0 }}>
              <svg width="100%" height="100%" viewBox="0 0 255 155" style={{ overflow: "visible" }}>
                <defs>
                  <filter id="greenGlow" x="-20%" y="-20%" width="140%" height="140%">
                    <feGaussianBlur stdDeviation="2.5" result="blur" />
                    <feComposite in="SourceGraphic" in2="blur" operator="over" />
                  </filter>
                </defs>

                {/* Render Edges */}
                {GRAPH_EDGES.map(e => {
                  const fromNode = GRAPH_NODES.find(n => n.id === e.from)!;
                  const toNode = GRAPH_NODES.find(n => n.id === e.to)!;
                  const edgeKey1 = `${e.from}-${e.to}`;
                  const edgeKey2 = `${e.to}-${e.from}`;
                  const isPath = currentGraphStep.pathEdges.includes(edgeKey1) || currentGraphStep.pathEdges.includes(edgeKey2);

                  const midX = (fromNode.x + toNode.x) / 2;
                  const midY = (fromNode.y + toNode.y) / 2;

                  return (
                    <g key={edgeKey1}>
                      <motion.line
                        x1={fromNode.x} y1={fromNode.y}
                        x2={toNode.x} y2={toNode.y}
                        stroke={isPath ? "#4ADE80" : "#1E293B"}
                        strokeWidth={isPath ? 2.5 : 1.2}
                        filter={isPath ? "url(#greenGlow)" : undefined}
                        animate={{ stroke: isPath ? "#4ADE80" : "#1E293B" }}
                        transition={{ duration: 0.25 }}
                      />
                      <rect x={midX - 7} y={midY - 6} width={14} height={12} rx={3} fill="#060A14" stroke={isPath ? "#4ADE80" : "#1E293B"} strokeWidth={0.8} />
                      <text x={midX} y={midY + 3.5} textAnchor="middle" fill={isPath ? "#4ADE80" : "#64748B"} fontSize="7.5" fontWeight="700" fontFamily="JetBrains Mono">
                        {e.weight}
                      </text>
                    </g>
                  );
                })}

                {/* Render Nodes */}
                {GRAPH_NODES.map(n => {
                  const isVisited = currentGraphStep.visited.includes(n.id);
                  const isActive = currentGraphStep.activeNode === n.id;
                  const isHovered = hoveredNode === n.id;

                  const strokeColor = isActive ? "#4ADE80" : isVisited ? "#22D3EE" : "#22D3EE";
                  const fillColor = isActive ? "rgba(74,222,128,0.25)" : isVisited ? "rgba(34,211,238,0.18)" : "#0C1526";

                  return (
                    <g
                      key={n.id}
                      style={{ cursor: "pointer" }}
                      onMouseEnter={() => setHoveredNode(n.id)}
                      onMouseLeave={() => setHoveredNode(null)}
                    >
                      {isActive && (
                        <circle cx={n.x} cy={n.y} r={18} fill="rgba(74,222,128,0.15)" stroke="#4ADE80" strokeWidth="0.8" strokeDasharray="2 2" />
                      )}
                      <circle
                        cx={n.x} cy={n.y} r={isHovered || isActive ? 13 : 11}
                        fill={fillColor} stroke={strokeColor} strokeWidth={isActive ? 2 : isHovered ? 1.8 : 1.2}
                        style={{ transition: "all 0.2s" }}
                      />
                      <text
                        x={n.x} y={n.y + 3.5} textAnchor="middle"
                        fill={isActive ? "#4ADE80" : isVisited ? "#E2E8F0" : "#94A3B8"}
                        fontSize="9" fontWeight="700" fontFamily="JetBrains Mono"
                      >
                        {n.label}
                      </text>
                    </g>
                  );
                })}
              </svg>
            </div>
          )}

          {/* Timeline scrubber slider */}
          <div style={{ padding: "6px 0 2px" }}>
            <input
              type="range" min={0} max={maxIdx} value={stepIdx}
              onChange={e => { setPlaying(false); setStepIdx(Number(e.target.value)); }}
              style={{ width: "100%", cursor: "pointer", accentColor: dataStructureMode === "array" ? "#22D3EE" : "#4ADE80", height: 4 }}
            />
            <div className="flex justify-between" style={{ marginTop: 1 }}>
              <span style={{ fontSize: 8, color: "#1E3A5F" }}>Start (Step 1)</span>
              <span style={{ fontSize: 8, color: "#1E3A5F" }}>Step {stepIdx + 1}/{maxIdx + 1}</span>
            </div>
          </div>
        </div>
      ) : (
        /* 3. VIEW STATE B: "Code & Stack" Selected (Execution Inspector View) */
        <div className="flex flex-col flex-1 min-h-0 overflow-hidden" style={{ padding: "6px 14px 0" }}>
          {/* Top 60% Pane: Code Editor with language selector tab */}
          <div style={{ flex: "6 1 0%", background: "#080D1B", border: "1px solid rgba(139,92,246,0.25)", borderRadius: 12, padding: "8px 10px", display: "flex", flexDirection: "column", minHeight: 0 }}>
            <div className="flex items-center justify-between mb-1.5 shrink-0">
              <div className="flex items-center gap-1.5">
                <Code2 size={11} style={{ color: "#A78BFA" }} />
                <span style={{ fontSize: 9.5, fontWeight: 700, color: "#E2E8F0" }}>Source Code Trace</span>
              </div>
              <div style={{ display: "flex", background: "#060A14", border: "1px solid rgba(255,255,255,0.08)", borderRadius: 6, padding: 1.5 }}>
                {(["Kotlin", "Python", "Java"] as const).map(lang => (
                  <button
                    key={lang}
                    onClick={() => setTraceLang(lang)}
                    style={{
                      padding: "2px 6px", borderRadius: 4, border: "none", fontSize: 7.5, fontWeight: 700, cursor: "pointer",
                      background: traceLang === lang ? "#8B5CF6" : "transparent",
                      color: traceLang === lang ? "#FFFFFF" : "#64748B"
                    }}
                  >
                    {lang}
                  </button>
                ))}
              </div>
            </div>

            {/* Syntax-highlighted code block */}
            <div style={{ flex: 1, background: "#060A14", border: "1px solid rgba(255,255,255,0.04)", borderRadius: 8, padding: "4px 0", overflowY: "auto" }}>
              {currentCodeLines.map(line => {
                const isActive = line.num === activeLineNum;
                return (
                  <div
                    key={line.num}
                    style={{
                      display: "flex", alignItems: "center", padding: "1.5px 8px", fontSize: 8.5,
                      background: isActive ? "rgba(34,211,238,0.15)" : "transparent",
                      borderLeft: isActive ? "3px solid #22D3EE" : "3px solid transparent"
                    }}
                  >
                    <span style={{ width: 18, color: isActive ? "#22D3EE" : "#334155", fontWeight: 700, flexShrink: 0 }}>{line.num}</span>
                    <code style={{ color: isActive ? "#22D3EE" : "#CBD5E1", whiteSpace: "pre" }}>{line.code}</code>
                  </div>
                );
              })}
            </div>
          </div>

          {/* Bottom 40% Split Pane: Live Variable Inspector & Memory Call Stack */}
          <div style={{ flex: "4 1 0%", marginTop: 6, display: "flex", flexDirection: "column", gap: 6, minHeight: 0 }}>
            {/* Variable Inspector */}
            <div style={{ background: "#0C1526", border: "1px solid rgba(34,211,238,0.2)", borderRadius: 10, padding: "6px 8px", shrink: 0 }}>
              <span style={{ fontSize: 7.5, color: "#64748B", fontWeight: 700, textTransform: "uppercase", letterSpacing: "0.1em" }}>Live Variable Inspector:</span>
              <div style={{ display: "flex", gap: 6, flexWrap: "wrap", marginTop: 3 }}>
                <span style={{ fontSize: 8, color: "#22D3EE", background: "rgba(34,211,238,0.08)", padding: "1px 5px", borderRadius: 4 }}>i = <strong>{Math.floor(stepIdx / 5)}</strong></span>
                <span style={{ fontSize: 8, color: "#22D3EE", background: "rgba(34,211,238,0.08)", padding: "1px 5px", borderRadius: 4 }}>swapped = <strong>{step.type === "swap" ? "true" : "false"}</strong></span>
                <span style={{ fontSize: 8, color: "#22D3EE", background: "rgba(34,211,238,0.08)", padding: "1px 5px", borderRadius: 4 }}>minDistance = <strong>12</strong></span>
              </div>
            </div>

            {/* Memory Call Stack */}
            <div style={{ flex: 1, background: "#0C1526", border: "1px solid rgba(139,92,246,0.2)", borderRadius: 10, padding: "6px 8px", overflowY: "auto" }}>
              <span style={{ fontSize: 7.5, color: "#A78BFA", fontWeight: 700, textTransform: "uppercase", letterSpacing: "0.1em", display: "block", marginBottom: 3 }}>
                Memory Call Stack Depth:
              </span>
              <div style={{ display: "flex", flexDirection: "column", gap: 3 }}>
                {callStackFrames.map((frame, i) => (
                  <div
                    key={frame.id}
                    style={{
                      padding: "4px 6px", borderRadius: 6,
                      background: frame.status === "active" ? "rgba(139,92,246,0.2)" : "#080D1B",
                      border: frame.status === "active" ? "1px solid #8B5CF6" : "1px solid rgba(255,255,255,0.04)",
                      display: "flex", alignItems: "center", justifyBetween: "space-between"
                    }}
                  >
                    <div>
                      <div style={{ fontSize: 8, fontWeight: 700, color: frame.status === "active" ? "#A78BFA" : "#94A3B8" }}>{frame.name}</div>
                      <div style={{ fontSize: 7, color: "#64748B" }}>{frame.params}</div>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Playback Control Bar */}
      <div className="flex items-center justify-between" style={{ padding: "4px 14px 6px", marginTop: "auto" }}>
        <button onClick={() => { setPlaying(false); setStepIdx(0); }} style={{ width: 28, height: 28, display: "flex", alignItems: "center", justifyContent: "center", background: "none", border: "none", cursor: "pointer" }}>
          <RotateCcw size={13} style={{ color: "#334155" }} />
        </button>
        <button onClick={() => setStepIdx(s => Math.max(0, s - 1))} style={{ width: 32, height: 32, borderRadius: 99, display: "flex", alignItems: "center", justifyContent: "center", background: "#0C1526", border: "1px solid rgba(255,255,255,0.07)", cursor: "pointer" }}>
          <SkipBack size={13} style={{ color: "#CBD5E1" }} />
        </button>
        <button
          onClick={() => setPlaying(p => !p)}
          style={{
            width: 44, height: 44, borderRadius: 99, display: "flex", alignItems: "center", justifyContent: "center",
            background: dataStructureMode === "array" ? "#22D3EE" : "#4ADE80",
            boxShadow: dataStructureMode === "array" ? "0 0 18px rgba(34,211,238,0.4)" : "0 0 18px rgba(74,222,128,0.4)",
            border: "none", cursor: "pointer"
          }}
        >
          {playing ? <Pause size={16} color="#060A14" /> : <Play size={16} color="#060A14" style={{ marginLeft: 2 }} />}
        </button>
        <button onClick={() => setStepIdx(s => Math.min(maxIdx, s + 1))} style={{ width: 32, height: 32, borderRadius: 99, display: "flex", alignItems: "center", justifyContent: "center", background: "#0C1526", border: "1px solid rgba(255,255,255,0.07)", cursor: "pointer" }}>
          <SkipForward size={13} style={{ color: "#CBD5E1" }} />
        </button>

        {/* 4. INTERACTIVE OVERLAY TRIGGER: AI Tutor Bot Button */}
        <button onClick={onTutorOpen} style={{ width: 32, height: 32, borderRadius: 99, display: "flex", alignItems: "center", justifyContent: "center", background: "rgba(139,92,246,0.15)", border: "1px solid rgba(139,92,246,0.35)", cursor: "pointer" }}>
          <Bot size={13} style={{ color: "#A78BFA" }} />
        </button>
      </div>

      {/* Bottom Nav Bar */}
      <div style={{ marginTop: "auto" }}>
        <BottomNav active="explore" />
      </div>

      {/* ── 2. CUSTOM INPUT MODAL (Drawer overlay from bottom) ── */}
      <AnimatePresence>
        {inputModalOpen && (
          <>
            {/* Backdrop */}
            <motion.div
              style={{ position: "absolute", inset: 0, background: "rgba(3,5,8,0.75)", zIndex: 30, backdropFilter: "blur(2px)" }}
              initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }}
              onClick={() => setInputModalOpen(false)}
            />

            {/* Modal Drawer */}
            <motion.div
              style={{
                position: "absolute", bottom: 0, left: 0, right: 0, zIndex: 40,
                background: "#0C1526", borderTop: "1px solid rgba(34,211,238,0.25)",
                borderRadius: "24px 24px 0 0", padding: "12px 16px 18px",
                display: "flex", flexDirection: "column", gap: 12
              }}
              initial={{ y: "100%" }} animate={{ y: 0 }} exit={{ y: "100%" }}
              transition={{ type: "spring", damping: 25, stiffness: 280 }}
            >
              {/* Handlebar */}
              <div style={{ display: "flex", justifyContent: "center" }}>
                <div style={{ width: 32, height: 3.5, borderRadius: 99, background: "rgba(255,255,255,0.15)" }} />
              </div>

              {/* Title */}
              <div className="flex items-center justify-between">
                <span style={{ fontSize: 13, fontWeight: 700, color: "#E2E8F0" }}>Customize Input</span>
                <button onClick={() => setInputModalOpen(false)} style={{ background: "none", border: "none", color: "#64748B", fontSize: 11, cursor: "pointer" }}>✕</button>
              </div>

              {/* Text input comma-separated */}
              <div>
                <label style={{ fontSize: 9.5, color: "#94A3B8", fontWeight: 600, display: "block", marginBottom: 4 }}>
                  Array Values (comma-separated):
                </label>
                <input
                  type="text"
                  value={inputValueStr}
                  onChange={e => setInputValueStr(e.target.value)}
                  style={{
                    width: "100%", background: "#060A14", border: "1px solid rgba(34,211,238,0.3)", borderRadius: 8,
                    padding: "7px 10px", fontSize: 11, color: "#22D3EE", fontFamily: "JetBrains Mono, monospace", outline: "none"
                  }}
                  placeholder="64, 34, 25, 12, 22"
                />
              </div>

              {/* Preset chips */}
              <div>
                <span style={{ fontSize: 9.5, color: "#94A3B8", fontWeight: 600, display: "block", marginBottom: 5 }}>
                  Preset Configurations:
                </span>
                <div style={{ display: "flex", gap: 5, flexWrap: "wrap" }}>
                  {["Random", "Sorted", "Reverse Sorted", "Nearly Sorted"].map(chip => {
                    const isSel = activePreset === chip;
                    return (
                      <button
                        key={chip}
                        onClick={() => applyPreset(chip)}
                        style={{
                          padding: "4px 8px", borderRadius: 6, fontSize: 9, fontWeight: 600, cursor: "pointer",
                          fontFamily: "JetBrains Mono, monospace",
                          background: isSel ? "rgba(34,211,238,0.2)" : "#080D1B",
                          color: isSel ? "#22D3EE" : "#64748B",
                          border: isSel ? "1px solid #22D3EE" : "1px solid rgba(255,255,255,0.06)"
                        }}
                      >
                        {chip}
                      </button>
                    );
                  })}
                </div>
              </div>

              {/* Range slider for Array Length (5 to 15) */}
              <div>
                <div className="flex justify-between" style={{ marginBottom: 3 }}>
                  <span style={{ fontSize: 9.5, color: "#94A3B8", fontWeight: 600 }}>Array Length:</span>
                  <span style={{ fontSize: 10, fontWeight: 700, color: "#22D3EE" }}>{arrayLength} elements</span>
                </div>
                <input
                  type="range" min={5} max={15} value={arrayLength}
                  onChange={e => {
                    const newLen = Number(e.target.value);
                    setArrayLength(newLen);
                    if (activePreset) {
                      // re-trigger preset with new len
                      const len = newLen;
                      let arr: number[] = [];
                      if (activePreset === "Random") arr = Array.from({ length: len }, () => Math.floor(Math.random() * 85) + 10);
                      else if (activePreset === "Sorted") arr = Array.from({ length: len }, (_, i) => Math.floor(10 + (i * 80) / len));
                      else if (activePreset === "Reverse Sorted") arr = Array.from({ length: len }, (_, i) => Math.floor(90 - (i * 80) / len));
                      else if (activePreset === "Nearly Sorted") {
                        arr = Array.from({ length: len }, (_, i) => Math.floor(10 + (i * 80) / len));
                        if (arr.length > 2) { const tmp = arr[1]; arr[1] = arr[2]; arr[2] = tmp; }
                      }
                      setCustomArray(arr);
                      setInputValueStr(arr.join(", "));
                    } else {
                      let arr = [...customArray];
                      if (arr.length < newLen) {
                        while (arr.length < newLen) arr.push(Math.floor(Math.random() * 70) + 15);
                      } else {
                        arr = arr.slice(0, newLen);
                      }
                      setCustomArray(arr);
                      setInputValueStr(arr.join(", "));
                    }
                  }}
                  style={{ width: "100%", accentColor: "#22D3EE", cursor: "pointer" }}
                />
              </div>

              {/* Primary button */}
              <button
                onClick={handleApplyInput}
                style={{
                  width: "100%", padding: "9px 0", borderRadius: 10, background: "#22D3EE",
                  border: "none", color: "#060A14", fontSize: 11, fontWeight: 700, cursor: "pointer",
                  fontFamily: "JetBrains Mono, monospace", marginTop: 2, boxShadow: "0 0 16px rgba(34,211,238,0.3)"
                }}
              >
                Apply & Reset Visualizer
              </button>
            </motion.div>
          </>
        )}
      </AnimatePresence>


    </div>
  );
}

// ─── Screen 03 — AI Tutor ─────────────────────────────────────────────────────

const FROZEN_STEP = ALL_STEPS[14];
const AI_TEXT = `Comparing elements at indices [2] and [3]: values 25 and 12.

Since 25 > 12, a swap is required — the smaller value needs to move leftward. This is Bubble Sort's fundamental operation: adjacent comparisons that "bubble" larger elements toward the end of the unsorted region.

After this swap, 12 moves to index 2 and 25 to index 3. The algorithm continues until a complete pass produces zero swaps.`;

function AITutorScreen() {
  return (
    <div className="relative flex flex-col h-full overflow-hidden" style={{ background: "#060A14", fontFamily: "JetBrains Mono, monospace" }}>
      <div style={{ position: "absolute", inset: 0, opacity: 0.25 }}>
        <div style={{ position: "absolute", bottom: "42%", left: 16, right: 16, height: 130, background: "#080D1B", borderRadius: 12, padding: "8px 10px 6px", display: "flex", alignItems: "flex-end", gap: 3 }}>
          {FROZEN_STEP.array.map((val, i) => (
            <div key={i} style={{ flex: 1, borderRadius: "2px 2px 0 0", background: barColor(i, FROZEN_STEP), height: Math.max(4, (val / MAX_VAL) * 110) }} />
          ))}
        </div>
        <div style={{ position: "absolute", top: 38, left: 16 }}>
          <div style={{ fontSize: 13, fontWeight: 700, color: "#E2E8F0" }}>Bubble Sort</div>
          <div style={{ fontSize: 10, color: "#475569", marginTop: 2 }}>Step 15 / {ALL_STEPS.length}</div>
        </div>
      </div>
      <div style={{ position: "absolute", inset: 0, background: "linear-gradient(to top, #060A14 48%, rgba(6,10,20,0.75) 70%, rgba(6,10,20,0.4) 100%)" }} />
      <div style={{ position: "relative", zIndex: 10 }}><StatusBar /></div>
      <motion.div
        style={{ position: "absolute", bottom: 0, left: 0, right: 0, background: "#0C1628", borderTop: "1px solid rgba(255,255,255,0.07)", borderRadius: "26px 26px 0 0", zIndex: 20 }}
        initial={{ y: 220 }}
        animate={{ y: 0 }}
        transition={{ type: "spring", damping: 26, stiffness: 270, delay: 0.05 }}
      >
        <div style={{ display: "flex", justifyContent: "center", padding: "10px 0 6px" }}>
          <div style={{ width: 36, height: 4, borderRadius: 99, background: "rgba(255,255,255,0.12)" }} />
        </div>
        <div className="flex items-center justify-between" style={{ padding: "0 18px 10px" }}>
          <div className="flex items-center gap-2">
            <div style={{ width: 30, height: 30, borderRadius: 9, background: "rgba(139,92,246,0.2)", display: "flex", alignItems: "center", justifyContent: "center" }}>
              <Sparkles size={13} style={{ color: "#A78BFA" }} />
            </div>
            <div>
              <div style={{ fontSize: 13, fontWeight: 700, color: "#E2E8F0" }}>AI Tutor</div>
              <div style={{ fontSize: 9, color: "#475569" }}>Powered by Claude</div>
            </div>
          </div>
          <div className="flex items-center gap-1.5" style={{ background: "rgba(139,92,246,0.12)", border: "1px solid rgba(139,92,246,0.3)", borderRadius: 99, padding: "4px 9px" }}>
            <div style={{ width: 5, height: 5, borderRadius: 99, background: "#A78BFA" }} />
            <span style={{ fontSize: 10, fontWeight: 600, color: "#C4B5FD" }}>Step 15</span>
          </div>
        </div>
        <div style={{ margin: "0 18px", height: 1, background: "rgba(255,255,255,0.05)" }} />
        <div style={{ padding: "12px 18px 8px" }}>
          <p style={{ fontSize: 10.5, color: "#94A3B8", lineHeight: 1.65, margin: 0, whiteSpace: "pre-line" }}>{AI_TEXT}</p>
        </div>
        <div style={{ margin: "4px 18px 10px", background: "rgba(34,211,238,0.05)", border: "1px solid rgba(34,211,238,0.13)", borderRadius: 10, padding: "9px 12px" }}>
          <div className="flex items-center justify-between" style={{ marginBottom: 5 }}>
            <span style={{ fontSize: 10, color: "#475569" }}>Time Complexity</span>
            <span style={{ fontSize: 10, fontWeight: 700, color: "#22D3EE" }}>O(n²)</span>
          </div>
          <div className="flex items-center justify-between">
            <span style={{ fontSize: 10, color: "#475569" }}>Space Complexity</span>
            <span style={{ fontSize: 10, fontWeight: 700, color: "#22D3EE" }}>O(1)</span>
          </div>
        </div>
        <div className="flex gap-2" style={{ padding: "0 18px 20px" }}>
          {["Trace Full", "Next Concept", "Why Swap?"].map(label => (
            <button key={label} style={{ flex: 1, padding: "7px 0", borderRadius: 10, fontSize: 10, fontWeight: 600, color: "#A78BFA", background: "rgba(139,92,246,0.08)", border: "1px solid rgba(139,92,246,0.22)", cursor: "pointer", fontFamily: "JetBrains Mono, monospace" }}>
              {label}
            </button>
          ))}
        </div>
      </motion.div>
    </div>
  );
}

// ─── Screen 06 — Interactive Practice Mode ─────────────────────────────────

function PracticeModeScreen() {
  const [selectedOption, setSelectedOption] = useState<number | null>(null);
  const [submitted, setSubmitted] = useState(false);

  const practiceArray = [12, 45, 23, 89, 34];
  const pivotIdx = 3; // 89 (value 89)
  const pointerLeft = 1; // 45
  const pointerRight = 4; // 34

  const options = [
    { id: 0, label: "Index 1 (Value: 45)", isCorrect: false },
    { id: 1, label: "Index 4 (Value: 34)", isCorrect: true },
    { id: 2, label: "Index 2 (Value: 23)", isCorrect: false },
    { id: 3, label: "Index 0 (Value: 12)", isCorrect: false },
  ];

  const handleReset = () => {
    setSelectedOption(null);
    setSubmitted(false);
  };

  return (
    <div className="flex flex-col h-full overflow-hidden" style={{ background: "#060A14", fontFamily: "JetBrains Mono, monospace" }}>
      <StatusBar />

      {/* 1. HEADER */}
      <div style={{ padding: "2px 14px 8px" }}>
        <div className="flex items-center gap-2" style={{ marginBottom: 6 }}>
          <div style={{ width: 24, height: 24, borderRadius: 6, background: "#0C1526", border: "1px solid rgba(255,255,255,0.08)", display: "flex", alignItems: "center", justifyContent: "center", flexShrink: 0 }}>
            <ChevronLeft size={13} style={{ color: "#94A3B8" }} />
          </div>
          <span className="truncate" style={{ fontSize: 11, fontWeight: 700, color: "#E2E8F0" }}>Practice Mode: QuickSort</span>
        </div>

        {/* Progress indicator */}
        <div className="flex items-center justify-between" style={{ marginBottom: 3 }}>
          <span style={{ fontSize: 8.5, color: "#94A3B8" }}>Question 3 of 5</span>
          <span style={{ fontSize: 8.5, fontWeight: 700, color: "#22D3EE" }}>60%</span>
        </div>
        <div style={{ height: 4, background: "#0C1526", borderRadius: 99, overflow: "hidden" }}>
          <div style={{ width: "60%", height: "100%", background: "#22D3EE", borderRadius: 99 }} />
        </div>
      </div>

      {/* Scrollable Body */}
      <div className="flex-1 overflow-y-auto" style={{ padding: "0 14px 12px", scrollbarWidth: "none", display: "flex", flexDirection: "column", gap: 10 }}>

        {/* 2. FROZEN VISUALIZER CANVAS */}
        <div style={{ background: "#080D1B", border: "1px solid rgba(255,255,255,0.06)", borderRadius: 12, padding: "8px 10px", position: "relative" }}>
          <div className="flex justify-between items-center" style={{ marginBottom: 6 }}>
            <span style={{ fontSize: 8.5, color: "#64748B", textTransform: "uppercase", letterSpacing: "0.1em" }}>Step 4 · QuickSort Partition</span>
            <span style={{ fontSize: 8, color: "#8B5CF6", background: "rgba(139,92,246,0.12)", padding: "1px 5px", borderRadius: 4 }}>Frozen Canvas</span>
          </div>

          <div style={{ height: 95, display: "flex", alignItems: "flex-end", gap: 5, padding: "4px 0 2px" }}>
            {practiceArray.map((val, idx) => {
              const isPivot = idx === pivotIdx;
              const isPointer = idx === pointerLeft || idx === pointerRight;
              const color = isPivot ? "#8B5CF6" : isPointer ? "#FBBF24" : "#1A2E50";

              return (
                <div key={idx} style={{ flex: 1, display: "flex", flexDirection: "column", alignItems: "center", height: "100%" }}>
                  {/* Pointer badge */}
                  <div style={{ height: 12, display: "flex", alignItems: "center" }}>
                    {isPivot && <span style={{ fontSize: 7, color: "#A78BFA", fontWeight: 700 }}>PIVOT</span>}
                    {idx === pointerLeft && <span style={{ fontSize: 7, color: "#FBBF24", fontWeight: 700 }}>L</span>}
                    {idx === pointerRight && <span style={{ fontSize: 7, color: "#FBBF24", fontWeight: 700 }}>R</span>}
                  </div>

                  <div style={{ flex: 1, width: "100%", display: "flex", alignItems: "flex-end" }}>
                    <div style={{ width: "100%", height: `${(val / 90) * 100}%`, background: color, borderRadius: "3px 3px 0 0", boxShadow: isPivot ? "0 0 10px rgba(139,92,246,0.5)" : isPointer ? "0 0 8px rgba(251,191,36,0.3)" : "none" }} />
                  </div>

                  <span style={{ fontSize: 8, color: isPivot ? "#A78BFA" : isPointer ? "#FBBF24" : "#64748B", marginTop: 2, fontWeight: 700 }}>{val}</span>
                </div>
              );
            })}
          </div>
        </div>

        {/* 3. QUESTION CARD */}
        <div style={{ background: "#0C1526", border: "1px solid rgba(255,255,255,0.05)", borderRadius: 12, padding: "10px 12px" }}>
          <div className="flex items-center justify-between" style={{ marginBottom: 6 }}>
            <span style={{ fontSize: 8, color: "#475569", fontWeight: 700, textTransform: "uppercase", letterSpacing: "0.1em" }}>Question</span>
            <span style={{ fontSize: 8.5, fontWeight: 700, color: "#FB923C", background: "rgba(251,146,60,0.12)", padding: "1px 6px", borderRadius: 4 }}>Medium</span>
          </div>
          <p style={{ fontSize: 10, color: "#E2E8F0", lineHeight: 1.45, margin: 0, fontWeight: 500 }}>
            Which element will be swapped with the pivot <span style={{ color: "#A78BFA", fontWeight: 700 }}>(89)</span> in the next step?
          </p>
        </div>

        {/* 4. MULTIPLE CHOICE OPTIONS */}
        <div style={{ display: "flex", flexDirection: "column", gap: 6 }}>
          {options.map((opt) => {
            const isSelected = selectedOption === opt.id;
            return (
              <button
                key={opt.id}
                onClick={() => !submitted && setSelectedOption(opt.id)}
                style={{
                  padding: "8px 10px", borderRadius: 9, textAlign: "left", cursor: "pointer",
                  background: isSelected ? "rgba(34,211,238,0.1)" : "#0C1526",
                  border: isSelected ? "1.5px solid #22D3EE" : "1px solid rgba(255,255,255,0.05)",
                  color: isSelected ? "#22D3EE" : "#CBD5E1",
                  fontSize: 9.5, fontWeight: 600, fontFamily: "JetBrains Mono, monospace",
                  transition: "all 0.15s", display: "flex", alignItems: "center", justifyBetween: "space-between"
                }}
              >
                <span style={{ flex: 1 }}>{opt.label}</span>
                <div style={{ width: 14, height: 14, borderRadius: 99, border: isSelected ? "4px solid #22D3EE" : "1px solid #334155", background: "#060A14" }} />
              </button>
            );
          })}
        </div>

        {/* Submit Button */}
        {!submitted && (
          <button
            onClick={() => selectedOption !== null && setSubmitted(true)}
            disabled={selectedOption === null}
            style={{
              padding: "9px 0", borderRadius: 10, background: selectedOption !== null ? "#22D3EE" : "#1A2540",
              border: "none", color: selectedOption !== null ? "#060A14" : "#475569",
              fontSize: 10.5, fontWeight: 700, cursor: selectedOption !== null ? "pointer" : "not-allowed",
              fontFamily: "JetBrains Mono, monospace", marginTop: 2, transition: "background 0.2s"
            }}
          >
            Submit Answer
          </button>
        )}

        {/* 5. FEEDBACK OVERLAY (Triggers on Submit) */}
        <AnimatePresence>
          {submitted && selectedOption !== null && (
            <motion.div
              initial={{ opacity: 0, y: 10 }} animate={{ opacity: 1, y: 0 }} exit={{ opacity: 0 }}
              style={{
                background: options[selectedOption].isCorrect ? "rgba(74,222,128,0.1)" : "rgba(239,68,68,0.1)",
                border: `1px solid ${options[selectedOption].isCorrect ? "#4ADE80" : "#EF4444"}`,
                borderRadius: 12, padding: "10px 12px", display: "flex", flexDirection: "column", gap: 8
              }}
            >
              <div className="flex items-center gap-2">
                <div style={{ width: 8, height: 8, borderRadius: 99, background: options[selectedOption].isCorrect ? "#4ADE80" : "#EF4444" }} />
                <span style={{ fontSize: 10.5, fontWeight: 700, color: options[selectedOption].isCorrect ? "#4ADE80" : "#EF4444" }}>
                  {options[selectedOption].isCorrect ? "Correct! Excellent partition analysis." : "Incorrect Choice"}
                </span>
              </div>

              <p style={{ fontSize: 9, color: "#CBD5E1", lineHeight: 1.5, margin: 0 }}>
                {options[selectedOption].isCorrect
                  ? "Index 4 (value 34) is the final smaller element scanned from right. Swapping 34 with pivot 89 correctly places 89 into its sorted partition slot."
                  : "QuickSort partition swaps the pivot element with the rightmost element smaller than pivot. Index 4 (value 34) is swapped with 89."
                }
              </p>

              <button
                onClick={handleReset}
                style={{
                  padding: "7px 0", borderRadius: 8, background: options[selectedOption].isCorrect ? "#4ADE80" : "#22D3EE",
                  border: "none", color: "#060A14", fontSize: 9.5, fontWeight: 700, cursor: "pointer",
                  fontFamily: "JetBrains Mono, monospace"
                }}
              >
                Next Question →
              </button>
            </motion.div>
          )}
        </AnimatePresence>
      </div>

      <BottomNav active="explore" />
    </div>
  );
}

// ─── Screen 04 — Settings ─────────────────────────────────────────────────────

type Lang = "Kotlin" | "Java" | "Python" | "C++";

function SettingsScreen() {
  const [language, setLanguage] = useState<Lang>("Kotlin");
  const [speed, setSpeed] = useState(50);
  const [offlineMode, setOfflineMode] = useState(true);
  const [notifications, setNotifications] = useState(true);
  const [darkLabels, setDarkLabels] = useState(false);
  const downloadPct = 68;

  const speedLabel = speed < 34 ? "Slow" : speed < 67 ? "Normal" : "Fast";
  const speedMs = speed < 34 ? "800ms" : speed < 67 ? "480ms" : "220ms";

  return (
    <div className="flex flex-col h-full overflow-hidden" style={{ background: "#060A14", fontFamily: "JetBrains Mono, monospace" }}>
      <StatusBar />

      {/* Header */}
      <div className="flex items-center gap-3" style={{ padding: "4px 16px 12px" }}>
        <div style={{ width: 28, height: 28, borderRadius: 8, background: "#0C1526", border: "1px solid rgba(255,255,255,0.07)", display: "flex", alignItems: "center", justifyContent: "center", cursor: "pointer", flexShrink: 0 }}>
          <ChevronLeft size={14} style={{ color: "#94A3B8" }} />
        </div>
        <span style={{ fontSize: 14, fontWeight: 700, color: "#E2E8F0" }}>Settings</span>
        <div style={{ marginLeft: "auto" }}>
          <div style={{ width: 28, height: 28, borderRadius: 8, background: "rgba(34,211,238,0.08)", border: "1px solid rgba(34,211,238,0.15)", display: "flex", alignItems: "center", justifyContent: "center" }}>
            <Settings size={12} style={{ color: "#22D3EE" }} />
          </div>
        </div>
      </div>

      {/* Scrollable body */}
      <div className="flex-1 overflow-y-auto" style={{ padding: "0 16px 16px", scrollbarWidth: "none", display: "flex", flexDirection: "column", gap: 10 }}>

        {/* ── Language Preference ── */}
        <div style={{ background: "#0C1526", border: "1px solid rgba(255,255,255,0.05)", borderRadius: 14, padding: "12px 13px" }}>
          <SectionLabel icon={Code2} text="Preferred Language" />
          <div style={{ display: "flex", gap: 6, flexWrap: "wrap" }}>
            {(["Kotlin", "Java", "Python", "C++"] as Lang[]).map(lang => (
              <button
                key={lang}
                onClick={() => setLanguage(lang)}
                style={{
                  padding: "5px 12px", borderRadius: 8, fontSize: 11, fontWeight: 600, cursor: "pointer",
                  fontFamily: "JetBrains Mono, monospace", transition: "all 0.15s",
                  background: language === lang ? "#22D3EE" : "#111D30",
                  color: language === lang ? "#060A14" : "#64748B",
                  border: language === lang ? "none" : "1px solid rgba(255,255,255,0.07)",
                }}
              >
                {lang}
              </button>
            ))}
          </div>
          <p style={{ fontSize: 9, color: "#334155", marginTop: 8, lineHeight: 1.5 }}>
            Code snippets use {language} syntax in step-by-step explanations.
          </p>
        </div>

        {/* ── Animation Speed ── */}
        <div style={{ background: "#0C1526", border: "1px solid rgba(255,255,255,0.05)", borderRadius: 14, padding: "12px 13px" }}>
          <SectionLabel icon={Gauge} text="Animation Playback Speed" />
          <div className="flex items-center justify-between" style={{ marginBottom: 8 }}>
            <span style={{ fontSize: 12, fontWeight: 700, color: "#CBD5E1" }}>{speedLabel}</span>
            <span style={{ fontSize: 10, color: "#475569" }}>{speedMs} / step</span>
          </div>
          <input
            type="range" min={0} max={100} value={speed}
            onChange={e => setSpeed(Number(e.target.value))}
            style={{ width: "100%", accentColor: "#22D3EE", cursor: "pointer", display: "block" }}
          />
          <div style={{ display: "flex", justifyContent: "space-between", marginTop: 4 }}>
            {["Slow", "Normal", "Fast"].map(l => (
              <span key={l} style={{ fontSize: 9, color: "#1E3A5F" }}>{l}</span>
            ))}
          </div>
        </div>

        {/* ── Offline Mode ── */}
        <div style={{ background: "#0C1526", border: "1px solid rgba(255,255,255,0.05)", borderRadius: 14, padding: "12px 13px" }}>
          <SectionLabel icon={WifiOff} text="Offline Mode" />

          {/* Toggle row */}
          <div className="flex items-center justify-between" style={{ marginBottom: 10 }}>
            <div>
              <span style={{ fontSize: 11, fontWeight: 600, color: "#CBD5E1" }}>Enable Offline Mode</span>
              <p style={{ fontSize: 9, color: "#475569", marginTop: 2 }}>Cache algorithms for use without internet</p>
            </div>
            <ToggleSwitch on={offlineMode} onToggle={() => setOfflineMode(v => !v)} />
          </div>

          {/* Download progress */}
          <div style={{ background: "#080D1B", border: "1px solid rgba(255,255,255,0.04)", borderRadius: 10, padding: "9px 11px", opacity: offlineMode ? 1 : 0.4 }}>
            <div className="flex justify-between" style={{ marginBottom: 5 }}>
              <span style={{ fontSize: 9, color: "#475569" }}>Download progress</span>
              <span style={{ fontSize: 9, fontWeight: 700, color: "#22D3EE" }}>{downloadPct}%</span>
            </div>
            <div style={{ height: 5, background: "#111D30", borderRadius: 99, overflow: "hidden" }}>
              <motion.div
                style={{ height: "100%", background: "linear-gradient(90deg, #22D3EE, #8B5CF6)", borderRadius: 99 }}
                initial={{ width: 0 }}
                animate={{ width: `${downloadPct}%` }}
                transition={{ duration: 1.2, ease: "easeOut", delay: 0.3 }}
              />
            </div>
            <div className="flex justify-between" style={{ marginTop: 5 }}>
              <span style={{ fontSize: 9, color: "#334155" }}>5 of 8 algorithms cached</span>
              <span style={{ fontSize: 9, color: "#334155" }}>12.4 MB</span>
            </div>
          </div>
        </div>

        {/* ── Preferences toggles ── */}
        <div style={{ background: "#0C1526", border: "1px solid rgba(255,255,255,0.05)", borderRadius: 14, padding: "12px 13px" }}>
          <SectionLabel icon={Sliders} text="Display" />
          {[
            { label: "Push Notifications", sub: "Step completions & study reminders", on: notifications, toggle: () => setNotifications(v => !v) },
            { label: "High Contrast Labels", sub: "Increase label visibility on bars", on: darkLabels, toggle: () => setDarkLabels(v => !v) },
          ].map((row, i) => (
            <div key={row.label}>
              {i > 0 && <div style={{ height: 1, background: "rgba(255,255,255,0.04)", margin: "8px 0" }} />}
              <div className="flex items-center justify-between">
                <div style={{ flex: 1, marginRight: 12 }}>
                  <span style={{ fontSize: 11, fontWeight: 600, color: "#CBD5E1" }}>{row.label}</span>
                  <p style={{ fontSize: 9, color: "#475569", marginTop: 2 }}>{row.sub}</p>
                </div>
                <ToggleSwitch on={row.on} onToggle={row.toggle} />
              </div>
            </div>
          ))}
        </div>

        {/* ── Data / danger zone ── */}
        <div style={{ background: "#0C1526", border: "1px solid rgba(255,255,255,0.05)", borderRadius: 14, padding: "12px 13px" }}>
          <SectionLabel icon={Database} text="Data Management" />
          <button style={{ width: "100%", display: "flex", alignItems: "center", justifyContent: "space-between", padding: "9px 11px", borderRadius: 9, background: "rgba(248,113,113,0.07)", border: "1px solid rgba(248,113,113,0.2)", cursor: "pointer", fontFamily: "JetBrains Mono, monospace" }}>
            <div className="flex items-center gap-2">
              <Trash2 size={12} style={{ color: "#F87171" }} />
              <span style={{ fontSize: 11, fontWeight: 600, color: "#F87171" }}>Clear Saved Data</span>
            </div>
            <span style={{ fontSize: 9, color: "#475569" }}>Bookmarks · History</span>
          </button>
        </div>

        {/* Version */}
        <div style={{ textAlign: "center", paddingTop: 2 }}>
          <span style={{ fontSize: 9, color: "#1E3A5F" }}>AlgoLens v2.4.1 · Build 204 · MIT License</span>
        </div>
      </div>
    </div>
  );
}

// ─── Screen 05 — Profile & Progress ──────────────────────────────────────────

function ProfileScreen() {
  return (
    <div className="flex flex-col h-full overflow-hidden" style={{ background: "#060A14", fontFamily: "JetBrains Mono, monospace" }}>
      <StatusBar />

      {/* Profile hero */}
      <div style={{ padding: "8px 16px 12px", display: "flex", alignItems: "center", gap: 14 }}>
        {/* Avatar */}
        <div style={{ position: "relative", flexShrink: 0 }}>
          <div style={{ width: 56, height: 56, borderRadius: 18, background: "linear-gradient(135deg, rgba(34,211,238,0.2), rgba(139,92,246,0.2))", border: "1.5px solid rgba(34,211,238,0.3)", display: "flex", alignItems: "center", justifyContent: "center" }}>
            <span style={{ fontSize: 18, fontWeight: 700, color: "#22D3EE", letterSpacing: "-0.04em" }}>DD</span>
          </div>
          <div style={{ position: "absolute", bottom: 2, right: 2, width: 11, height: 11, borderRadius: 99, background: "#4ADE80", border: "2px solid #060A14" }} />
        </div>
        <div className="flex-1 min-w-0">
          <div style={{ fontSize: 14, fontWeight: 700, color: "#E2E8F0" }}>Duke Ducky</div>
          <div style={{ fontSize: 10, color: "#475569", marginTop: 2 }}>@quacky · CS Student · Year 3</div>
          <div style={{ display: "flex", gap: 5, marginTop: 6 }}>
            <span style={{ fontSize: 9, fontWeight: 700, color: "#22D3EE", background: "rgba(34,211,238,0.1)", border: "1px solid rgba(34,211,238,0.2)", borderRadius: 99, padding: "2px 8px" }}>Beginner</span>
            <span style={{ fontSize: 9, fontWeight: 700, color: "#FB923C", background: "rgba(251,146,60,0.1)", border: "1px solid rgba(251,146,60,0.2)", borderRadius: 99, padding: "2px 8px" }}>🔥 Streak Active</span>
          </div>
        </div>
      </div>

      {/* Stats row */}
      <div style={{ display: "flex", gap: 8, padding: "0 16px 12px" }}>
        {[
          { value: "14", label: "Mastered", color: "#22D3EE", sub: "algorithms" },
          { value: "12", label: "Day Streak", color: "#FB923C", sub: "days in a row" },
          { value: "48", label: "Sessions", color: "#C084FC", sub: "total" },
        ].map(s => (
          <div key={s.label} style={{ flex: 1, background: "#0C1526", border: "1px solid rgba(255,255,255,0.05)", borderRadius: 12, padding: "10px 8px", textAlign: "center" }}>
            <div style={{ fontSize: 20, fontWeight: 700, color: s.color, lineHeight: 1 }}>{s.value}</div>
            <div style={{ fontSize: 9, color: "#CBD5E1", marginTop: 3, fontWeight: 600 }}>{s.label}</div>
            <div style={{ fontSize: 8, color: "#334155", marginTop: 2 }}>{s.sub}</div>
          </div>
        ))}
      </div>

      {/* Weekly activity chart */}
      <div style={{ margin: "0 16px 12px", background: "#0C1526", border: "1px solid rgba(255,255,255,0.05)", borderRadius: 12, padding: "11px 13px" }}>
        <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", marginBottom: 10 }}>
          <div className="flex items-center gap-2">
            <BarChart2 size={11} style={{ color: "#22D3EE" }} />
            <span style={{ fontSize: 9, fontWeight: 700, color: "#334155", letterSpacing: "0.12em", textTransform: "uppercase" }}>Weekly Activity</span>
          </div>
          <span style={{ fontSize: 9, color: "#1E3A5F" }}>30 sessions this week</span>
        </div>
        <div style={{ display: "flex", alignItems: "flex-end", gap: 4, height: 52 }}>
          {WEEKLY_ACTIVITY.map((val, i) => {
            const days = ["M", "T", "W", "T", "F", "S", "S"];
            const isToday = i === 6;
            const pct = (val / 7) * 100;
            return (
              <div key={i} style={{ flex: 1, display: "flex", flexDirection: "column", alignItems: "center", gap: 4, height: "100%" }}>
                <div style={{ flex: 1, width: "100%", display: "flex", flexDirection: "column", justifyContent: "flex-end" }}>
                  <motion.div
                    style={{ borderRadius: "2px 2px 0 0", background: isToday ? "#22D3EE" : "rgba(34,211,238,0.22)" }}
                    initial={{ height: 0 }}
                    animate={{ height: `${pct}%` }}
                    transition={{ duration: 0.6, delay: i * 0.07, ease: "easeOut" }}
                  />
                </div>
                <span style={{ fontSize: 8, color: isToday ? "#22D3EE" : "#334155", fontWeight: isToday ? 700 : 400 }}>{days[i]}</span>
              </div>
            );
          })}
        </div>
      </div>

      {/* Bookmarked algorithms */}
      <div style={{ flex: 1, padding: "0 16px", overflow: "hidden", display: "flex", flexDirection: "column", minHeight: 0 }}>
        <div className="flex items-center justify-between" style={{ marginBottom: 8 }}>
          <div className="flex items-center gap-2">
            <Bookmark size={11} style={{ color: "#22D3EE" }} />
            <span style={{ fontSize: 9, fontWeight: 700, color: "#334155", letterSpacing: "0.12em", textTransform: "uppercase" }}>Bookmarked</span>
          </div>
          <span style={{ fontSize: 9, color: "#22D3EE", cursor: "pointer" }}>View all →</span>
        </div>
        <div style={{ display: "flex", flexDirection: "column", gap: 5, overflowY: "auto", scrollbarWidth: "none" }}>
          {BOOKMARKED_ALGORITHMS.map(algo => <AlgoCard key={algo.id} algo={algo} />)}
        </div>
      </div>

      {/* Bottom nav */}
      <BottomNav active="profile" />
    </div>
  );
}

// ─── Phone Frame ─────────────────────────────────────────────────────────────

function PhoneFrame({ children, label, num }: { children: React.ReactNode; label: string; num: string }) {
  return (
    <div className="flex flex-col items-center" style={{ gap: 18, fontFamily: "JetBrains Mono, monospace" }}>
      <div className="flex items-center gap-2">
        <span style={{ fontSize: 10, color: "#1E3A5F", letterSpacing: "0.15em" }}>{num}</span>
        <div style={{ width: 28, height: 1, background: "rgba(255,255,255,0.08)" }} />
        <span style={{ fontSize: 11, color: "#475569" }}>{label}</span>
      </div>
      <div style={{ position: "relative", width: 278 }}>
        <div style={{ position: "absolute", right: -3, top: 88, width: 3, height: 30, background: "#141E35", borderRadius: "0 3px 3px 0" }} />
        <div style={{ position: "absolute", left: -3, top: 78, width: 3, height: 24, background: "#141E35", borderRadius: "3px 0 0 3px" }} />
        <div style={{ position: "absolute", left: -3, top: 110, width: 3, height: 24, background: "#141E35", borderRadius: "3px 0 0 3px" }} />
        <div style={{ width: 278, height: 584, borderRadius: 44, overflow: "hidden", background: "#060A14", border: "1.5px solid rgba(255,255,255,0.07)", boxShadow: "0 0 0 1px rgba(0,0,0,0.6), 0 32px 80px rgba(0,0,0,0.7), 0 0 50px rgba(34,211,238,0.04)" }}>
          {children}
        </div>
      </div>
    </div>
  );
}

// ─── Row Divider ──────────────────────────────────────────────────────────────

function RowDivider({ label }: { label: string }) {
  return (
    <div style={{ display: "flex", alignItems: "center", gap: 14, width: "100%", maxWidth: 960, padding: "8px 0" }}>
      <div style={{ flex: 1, height: 1, background: "rgba(255,255,255,0.05)" }} />
      <span style={{ fontSize: 9, fontWeight: 700, color: "#1E3A5F", letterSpacing: "0.15em", textTransform: "uppercase", flexShrink: 0 }}>{label}</span>
      <div style={{ flex: 1, height: 1, background: "rgba(255,255,255,0.05)" }} />
    </div>
  );
}

// ─── Root App ─────────────────────────────────────────────────────────────────

export default function App() {
  const [tutorOpen, setTutorOpen] = useState(false);

  return (
    <div style={{ minHeight: "100vh", background: "#030508", backgroundImage: "radial-gradient(ellipse 80% 40% at 50% 0%, rgba(34,211,238,0.05) 0%, transparent 60%)", display: "flex", flexDirection: "column", alignItems: "center", padding: "48px 24px 72px", fontFamily: "JetBrains Mono, monospace" }}>

      {/* Page header */}
      <div style={{ textAlign: "center", marginBottom: 44 }}>
        <div className="flex items-center justify-center gap-2" style={{ marginBottom: 10 }}>
          <div style={{ width: 34, height: 34, borderRadius: 11, background: "rgba(34,211,238,0.12)", border: "1px solid rgba(34,211,238,0.22)", display: "flex", alignItems: "center", justifyContent: "center" }}>
            <Zap size={16} style={{ color: "#22D3EE" }} />
          </div>
          <h1 style={{ fontSize: 22, fontWeight: 700, color: "#E2E8F0", letterSpacing: "-0.04em" }}>AlgoLens</h1>
        </div>
        <p style={{ fontSize: 11, color: "#334155" }}>Algorithm Visualization & Learning Platform · Android UI · 7 Showcase Screens</p>
        <div className="flex items-center justify-center gap-3" style={{ marginTop: 12 }}>
          {["Dark Theme", "Offline Ready", "AI-Powered", "Practice Mode", "7 Phone Frames"].map(tag => (
            <span key={tag} style={{ fontSize: 9, fontWeight: 600, color: "#22D3EE", padding: "3px 9px", borderRadius: 99, background: "rgba(34,211,238,0.07)", border: "1px solid rgba(34,211,238,0.15)", letterSpacing: "0.06em", textTransform: "uppercase" }}>
              {tag}
            </span>
          ))}
        </div>
      </div>

      {/* ── Row 1: Core Visualization & AI Suite ── */}
      <RowDivider label="Core Visualization & AI Suite" />
      <div style={{ display: "flex", flexWrap: "wrap", gap: 32, justifyContent: "center", alignItems: "flex-start", maxWidth: 1020, width: "100%", marginTop: 28, marginBottom: 44 }}>
        <PhoneFrame num="01" label="Dashboard & Search">
          <DashboardScreen />
        </PhoneFrame>
        <PhoneFrame num="02" label="Visualizer (Array Mode)">
          <VisualizerScreen onTutorOpen={() => setTutorOpen(true)} initialMode="array" />
        </PhoneFrame>
        <PhoneFrame num="03" label="Visualizer (Graph Mode)">
          <VisualizerScreen onTutorOpen={() => setTutorOpen(true)} initialMode="graph" />
        </PhoneFrame>
      </div>

      {/* ── Row 2: Practice & User Suite ── */}
      <RowDivider label="Interactive Practice & User Suite" />
      <div style={{ display: "flex", flexWrap: "wrap", gap: 32, justifyContent: "center", alignItems: "flex-start", maxWidth: 1020, width: "100%", marginTop: 28 }}>
        <PhoneFrame num="04" label="AI Tutor Assistant">
          <AITutorScreen />
        </PhoneFrame>
        <PhoneFrame num="05" label="Interactive Practice Mode">
          <PracticeModeScreen />
        </PhoneFrame>
        <PhoneFrame num="06" label="Settings & Preferences">
          <SettingsScreen />
        </PhoneFrame>
        <PhoneFrame num="07" label="Profile & Progress Tracker">
          <ProfileScreen />
        </PhoneFrame>
      </div>

      {/* Hint */}
      <div className="flex items-center gap-2" style={{ marginTop: 48 }}>
        <div style={{ width: 4, height: 4, borderRadius: 99, background: "#22D3EE", opacity: 0.5 }} />
        <span style={{ fontSize: 10, color: "#1E3A5F" }}>All 7 distinct phone screens feature complete Android navigation & bottom bars</span>
        <div style={{ width: 4, height: 4, borderRadius: 99, background: "#8B5CF6", opacity: 0.5 }} />
      </div>

      {/* AI Tutor overlay (from Visualizer bot button) */}
      <AnimatePresence>
        {tutorOpen && (
          <motion.div
            style={{ position: "fixed", inset: 0, zIndex: 50, background: "rgba(3,5,8,0.85)", display: "flex", alignItems: "center", justifyContent: "center", backdropFilter: "blur(4px)" }}
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            onClick={() => setTutorOpen(false)}
          >
            <motion.div
              style={{ position: "relative" }}
              initial={{ scale: 0.92, y: 20 }}
              animate={{ scale: 1, y: 0 }}
              exit={{ scale: 0.95, y: 10 }}
              onClick={e => e.stopPropagation()}
            >
              <PhoneFrame num="04" label="AI Tutor — live overlay">
                <AITutorScreen />
              </PhoneFrame>
            </motion.div>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
}
