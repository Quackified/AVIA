package com.example.algolens.ui.visualizer

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.algolens.R
import com.example.algolens.data.BubbleSort
import com.example.algolens.databinding.FragmentVisualizerBinding
import com.example.algolens.databinding.ItemCodeLineBinding
import com.example.algolens.model.GraphEdge
import com.example.algolens.model.GraphNode
import com.example.algolens.model.SortStep
import com.example.algolens.model.StepType
import com.example.algolens.ui.tutor.AITutorFragment

class VisualizerFragment : Fragment() {

    private var _binding: FragmentVisualizerBinding? = null
    private val binding get() = _binding!!

    private var algorithmId: Int = -1
    private var algorithmName: String = ""

    // Animation state
    private var steps: List<SortStep> = emptyList()
    private var currentStepIdx = 0
    private var isPlaying = false
    private val handler = Handler(Looper.getMainLooper())
    private val animationRunnable = object : Runnable {
        override fun run() {
            if (isPlaying && currentStepIdx < steps.size - 1) {
                currentStepIdx++
                updateUiForStep(currentStepIdx)
                handler.postDelayed(this, 500) // 500ms between steps
            } else {
                isPlaying = false
                updatePlayPauseButton()
            }
        }
    }

    private val bubbleSortCode = listOf(
        "fun bubbleSort(arr: IntArray) {",
        "  val n = arr.size",
        "  for (i in 0 until n - 1) {",
        "    for (j in 0 until n - i - 1) {",
        "      if (arr[j] > arr[j + 1]) {",
        "        val temp = arr[j]",
        "        arr[j] = arr[j + 1]",
        "        arr[j + 1] = temp",
        "      }",
        "    }",
        "  }",
        "}"
    )

    private val codeViews = mutableListOf<ItemCodeLineBinding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            algorithmId = it.getInt(ARG_ALGO_ID)
            algorithmName = it.getString(ARG_ALGO_NAME) ?: ""
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVisualizerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupHeader()
        setupControls()
        setupCodeListing()
        setupInitialData()
    }

    private fun setupCodeListing() {
        binding.layoutCodeListing.removeAllViews()
        codeViews.clear()
        
        bubbleSortCode.forEachIndexed { index, line ->
            val itemBinding = ItemCodeLineBinding.inflate(layoutInflater, binding.layoutCodeListing, false)
            itemBinding.tvLineNumber.text = (index + 1).toString()
            itemBinding.tvCodeText.text = line
            binding.layoutCodeListing.addView(itemBinding.root)
            codeViews.add(itemBinding)
        }
    }

    private fun setupInitialData() {
        // Generate Bubble Sort steps
        val input = listOf(64, 34, 25, 12, 22, 11, 90)
        steps = BubbleSort.generateSteps(input)
        
        // Setup scrubber
        binding.stepScrubber.max = steps.size - 1
        binding.stepScrubber.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    isPlaying = false
                    updatePlayPauseButton()
                    currentStepIdx = progress
                    updateUiForStep(currentStepIdx)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Initial UI state
        updateUiForStep(0)

        // Sample Graph Data
        val sampleNodes = listOf(
            GraphNode("A", 45f, 35f, "A"),
            GraphNode("B", 125f, 25f, "B"),
            GraphNode("C", 205f, 40f, "C"),
            GraphNode("D", 50f, 110f, "D"),
            GraphNode("E", 130f, 120f, "E"),
            GraphNode("F", 210f, 105f, "F")
        )
        val sampleEdges = listOf(
            GraphEdge("A", "B", 4),
            GraphEdge("A", "D", 2),
            GraphEdge("B", "C", 5),
            GraphEdge("B", "E", 1),
            GraphEdge("D", "E", 3),
            GraphEdge("E", "C", 2),
            GraphEdge("E", "F", 4),
            GraphEdge("C", "F", 3)
        )
        binding.graphVisualizer.setGraph(sampleNodes, sampleEdges)
        binding.graphVisualizer.updateState(setOf("A", "B", "D", "E"), "E", setOf("A-B", "B-E"))
    }

    private fun updateUiForStep(index: Int) {
        if (index < 0 || index >= steps.size) return
        
        val step = steps[index]
        
        // Update the bar visualizer data
        binding.barVisualizer.setData(step.array)
        
        // Update highlights
        val highlightColor = when (step.type) {
            StepType.COMPARE -> Color.parseColor("#FBBF24") // Yellow
            StepType.SWAP -> Color.parseColor("#F87171") // Red
            StepType.DONE -> Color.parseColor("#22D3EE") // Cyan
        }
        
        val highlights = mutableMapOf<Int, Int>()
        step.indices.forEach { highlights[it] = highlightColor }
        binding.barVisualizer.setHighlights(highlights)

        // Update text labels
        binding.tvStepDescription.text = step.description
        binding.tvStepCounter.text = "Step ${index + 1} of ${steps.size}"
        binding.stepScrubber.progress = index

        // Update Code Highlight
        updateCodeHighlight(step)
    }

    private fun updateCodeHighlight(step: SortStep) {
        // Reset all highlights
        codeViews.forEach { 
            it.layoutCodeLine.setBackgroundColor(Color.TRANSPARENT)
            it.tvCodeText.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary))
        }

        // Determine which line to highlight based on step type and indices
        val lineToHighlight = when (step.type) {
            StepType.COMPARE -> 5 // if (arr[j] > arr[j+1])
            StepType.SWAP -> 6    // swap lines
            StepType.DONE -> -1
        }

        if (lineToHighlight in 1..codeViews.size) {
            val view = codeViews[lineToHighlight - 1]
            view.layoutCodeLine.setBackgroundColor(Color.parseColor("#2622D3EE")) // Transparent Cyan
            view.tvCodeText.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary_cyan))
        }

        // Mock variable updates
        val iVal = if (step.indices.isNotEmpty()) step.indices[0] / 5 else 0
        val jVal = if (step.indices.isNotEmpty()) step.indices[0] % 5 else 0
        binding.tvVarI.text = "i = $iVal"
        binding.tvVarJ.text = "j = $jVal"
    }

    private fun setupHeader() {
        binding.tvAlgoName.text = algorithmName
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupControls() {
        binding.btnPlayPause.setOnClickListener {
            togglePlayback()
        }

        binding.btnSkipForward.setOnClickListener {
            if (currentStepIdx < steps.size - 1) {
                isPlaying = false
                updatePlayPauseButton()
                currentStepIdx++
                updateUiForStep(currentStepIdx)
            }
        }

        binding.btnSkipBack.setOnClickListener {
            if (currentStepIdx > 0) {
                isPlaying = false
                updatePlayPauseButton()
                currentStepIdx--
                updateUiForStep(currentStepIdx)
            }
        }

        binding.btnReset.setOnClickListener {
            isPlaying = false
            updatePlayPauseButton()
            currentStepIdx = 0
            updateUiForStep(currentStepIdx)
        }

        binding.btnAiTutor.setOnClickListener {
            val step = if (currentStepIdx < steps.size) steps[currentStepIdx] else null
            val explanation = step?.description ?: "Select a step to see an explanation."
            
            val tutorSheet = AITutorFragment.newInstance(currentStepIdx + 1, explanation)
            tutorSheet.show(parentFragmentManager, "ai_tutor")
        }

        binding.layoutEditInput.setOnClickListener {
            showCustomizeInputDialog()
        }

        // DS Mode Toggles
        binding.btnDsArray.setOnClickListener {
            updateDataStructureMode(isGraph = false)
        }
        binding.btnDsGraph.setOnClickListener {
            updateDataStructureMode(isGraph = true)
        }

        // Perspective Toggles
        binding.btnViewCanvas.setOnClickListener {
            updatePerspectiveStyle(isCanvas = true)
        }
        binding.btnViewCode.setOnClickListener {
            updatePerspectiveStyle(isCanvas = false)
        }
    }

    private fun showCustomizeInputDialog() {
        val dialog = CustomizeInputDialogFragment.newInstance()
        dialog.setOnInputAppliedListener { newList ->
            // Stop current animation
            isPlaying = false
            updatePlayPauseButton()
            handler.removeCallbacks(animationRunnable)
            
            // Generate new steps
            steps = BubbleSort.generateSteps(newList)
            currentStepIdx = 0
            binding.stepScrubber.max = steps.size - 1
            
            // Update UI
            updateUiForStep(0)
        }
        dialog.show(parentFragmentManager, CustomizeInputDialogFragment.TAG)
    }

    private fun togglePlayback() {
        isPlaying = !isPlaying
        updatePlayPauseButton()
        if (isPlaying) {
            if (currentStepIdx >= steps.size - 1) {
                currentStepIdx = 0
                updateUiForStep(currentStepIdx)
            }
            handler.post(animationRunnable)
        } else {
            handler.removeCallbacks(animationRunnable)
        }
    }

    private fun updatePlayPauseButton() {
        binding.ivPlayPause.setImageResource(
            if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
        )
    }

    private fun updateDataStructureMode(isGraph: Boolean) {
        if (isGraph) {
            binding.barVisualizer.visibility = View.GONE
            binding.graphVisualizer.visibility = View.VISIBLE
            
            binding.btnDsGraph.setBackgroundResource(R.drawable.chip_background_selector)
            binding.btnDsGraph.backgroundTintList = android.content.res.ColorStateList.valueOf(
                ContextCompat.getColor(requireContext(), R.color.success_green)
            ).withAlpha(51)
            binding.btnDsGraph.setTextColor(ContextCompat.getColor(requireContext(), R.color.success_green))

            binding.btnDsArray.setBackgroundResource(R.drawable.card_background)
            binding.btnDsArray.backgroundTintList = null
            binding.btnDsArray.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_muted))
        } else {
            binding.barVisualizer.visibility = View.VISIBLE
            binding.graphVisualizer.visibility = View.GONE

            binding.btnDsArray.setBackgroundResource(R.drawable.chip_background_selector)
            binding.btnDsArray.backgroundTintList = android.content.res.ColorStateList.valueOf(
                ContextCompat.getColor(requireContext(), R.color.primary_cyan)
            ).withAlpha(51)
            binding.btnDsArray.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary_cyan))

            binding.btnDsGraph.setBackgroundResource(R.drawable.card_background)
            binding.btnDsGraph.backgroundTintList = null
            binding.btnDsGraph.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_muted))
        }
    }

    private fun updatePerspectiveStyle(isCanvas: Boolean) {
        if (isCanvas) {
            binding.barVisualizer.visibility = View.VISIBLE
            binding.layoutCodeStack.visibility = View.GONE

            binding.btnViewCanvas.setBackgroundResource(R.drawable.chip_background_selector)
            binding.btnViewCanvas.setTextColor(ContextCompat.getColor(requireContext(), R.color.bg_dark))
            
            binding.btnViewCode.background = null
            binding.btnViewCode.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_muted))
        } else {
            binding.barVisualizer.visibility = View.GONE
            binding.layoutCodeStack.visibility = View.VISIBLE

            binding.btnViewCode.setBackgroundResource(R.drawable.chip_background_selector)
            binding.btnViewCode.setTextColor(ContextCompat.getColor(requireContext(), R.color.bg_dark))
            
            binding.btnViewCanvas.background = null
            binding.btnViewCanvas.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_muted))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(animationRunnable)
        _binding = null
    }

    companion object {
        private const val ARG_ALGO_ID = "algo_id"
        private const val ARG_ALGO_NAME = "algo_name"

        fun newInstance(id: Int, name: String) = VisualizerFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_ALGO_ID, id)
                putString(ARG_ALGO_NAME, name)
            }
        }
    }
}
