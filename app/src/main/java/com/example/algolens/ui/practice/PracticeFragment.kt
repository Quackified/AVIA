package com.example.algolens.ui.practice

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.algolens.R
import com.example.algolens.databinding.FragmentPracticeBinding

class PracticeFragment : Fragment() {

    private var _binding: FragmentPracticeBinding? = null
    private val binding get() = _binding!!

    private var selectedOptionIndex: Int? = null
    private val correctOptionIndex = 1 // Index 4 (Value: 34)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPracticeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupPracticeVisualizer()
        setupOptions()

        binding.btnSubmit.setOnClickListener {
            if (selectedOptionIndex != null) {
                showFeedback()
            }
        }

        binding.btnNext.setOnClickListener {
            resetState()
        }
    }

    private fun setupPracticeVisualizer() {
        val practiceArray = listOf(12, 45, 23, 89, 34)
        binding.practiceVisualizer.setData(practiceArray)
        
        // Highlights: Pivot (Purple), Pointers (Yellow)
        val highlights = mapOf(
            3 to Color.parseColor("#8B5CF6"), // Pivot 89
            1 to Color.parseColor("#FBBF24"), // Pointer L 45
            4 to Color.parseColor("#FBBF24")  // Pointer R 34
        )
        binding.practiceVisualizer.setHighlights(highlights)
    }

    private fun setupOptions() {
        val options = listOf(binding.btnOption0, binding.btnOption1, binding.btnOption2)
        
        options.forEachIndexed { index, textView ->
            textView.setOnClickListener {
                selectOption(index, options)
            }
        }
    }

    private fun selectOption(index: Int, options: List<TextView>) {
        selectedOptionIndex = index
        options.forEachIndexed { i, textView ->
            textView.isSelected = (i == index)
        }
    }

    private fun showFeedback() {
        val isCorrect = selectedOptionIndex == correctOptionIndex
        
        binding.layoutFeedback.visibility = View.VISIBLE
        binding.btnSubmit.visibility = View.GONE
        
        if (isCorrect) {
            binding.layoutFeedback.backgroundTintList = android.content.res.ColorStateList.valueOf(
                ContextCompat.getColor(requireContext(), R.color.success_green)
            ).withAlpha(26)
            binding.tvFeedbackTitle.text = "Correct! Excellent partition analysis."
            binding.tvFeedbackTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.success_green))
        } else {
            binding.layoutFeedback.backgroundTintList = android.content.res.ColorStateList.valueOf(
                ContextCompat.getColor(requireContext(), R.color.error_red)
            ).withAlpha(26)
            binding.tvFeedbackTitle.text = "Incorrect Choice"
            binding.tvFeedbackTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.error_red))
            binding.tvFeedbackText.text = "QuickSort partition swaps the pivot element with the rightmost element smaller than pivot. Index 4 (value 34) is swapped with 89."
        }
    }

    private fun resetState() {
        selectedOptionIndex = null
        val options = listOf(binding.btnOption0, binding.btnOption1, binding.btnOption2)
        options.forEach { it.isSelected = false }
        binding.layoutFeedback.visibility = View.GONE
        binding.btnSubmit.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
