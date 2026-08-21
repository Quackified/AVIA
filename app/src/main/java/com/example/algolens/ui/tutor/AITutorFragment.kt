package com.example.algolens.ui.tutor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.algolens.databinding.FragmentAiTutorBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AITutorFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentAiTutorBinding? = null
    private val binding get() = _binding!!

    private var stepNumber: Int = 0
    private var explanation: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            stepNumber = it.getInt(ARG_STEP_NUM)
            explanation = it.getString(ARG_EXPLANATION) ?: ""
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAiTutorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvTutorStepBadge.text = "Step $stepNumber"
        binding.tvExplanation.text = explanation
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_STEP_NUM = "step_num"
        private const val ARG_EXPLANATION = "explanation"

        fun newInstance(stepNum: Int, explanation: String) = AITutorFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_STEP_NUM, stepNum)
                putString(ARG_EXPLANATION, explanation)
            }
        }
    }
}
