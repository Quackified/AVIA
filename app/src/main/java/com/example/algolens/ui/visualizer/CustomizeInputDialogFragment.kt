package com.example.algolens.ui.visualizer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.DialogFragment
import com.example.algolens.databinding.DialogCustomizeInputBinding
import kotlin.random.Random

class CustomizeInputDialogFragment : DialogFragment() {

    private var _binding: DialogCustomizeInputBinding? = null
    private val binding get() = _binding!!

    private var onInputApplied: ((List<Int>) -> Unit)? = null
    private var currentLength = 7

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogCustomizeInputBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupPresets()
        setupSeekBar()

        binding.btnApply.setOnClickListener {
            val inputStr = binding.etInputValues.text.toString()
            val list = if (inputStr.isNotEmpty()) {
                inputStr.split(",").mapNotNull { it.trim().toIntOrNull() }
            } else {
                generateRandomList(currentLength)
            }
            
            if (list.isNotEmpty()) {
                onInputApplied?.invoke(list)
                dismiss()
            }
        }

        binding.btnClose.setOnClickListener { dismiss() }
    }

    private fun setupPresets() {
        binding.btnPresetRandom.setOnClickListener {
            binding.etInputValues.setText(generateRandomList(currentLength).joinToString(", "))
        }
        binding.btnPresetSorted.setOnClickListener {
            binding.etInputValues.setText((1..currentLength).map { it * 10 }.joinToString(", "))
        }
        binding.btnPresetReverse.setOnClickListener {
            binding.etInputValues.setText((currentLength downTo 1).map { it * 10 }.joinToString(", "))
        }
    }

    private fun setupSeekBar() {
        binding.sbLength.progress = currentLength - 5
        binding.tvLengthValue.text = "$currentLength elements"

        binding.sbLength.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                currentLength = progress + 5
                binding.tvLengthValue.text = "$currentLength elements"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun generateRandomList(size: Int): List<Int> {
        return List(size) { Random.nextInt(10, 99) }
    }

    fun setOnInputAppliedListener(listener: (List<Int>) -> Unit) {
        this.onInputApplied = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "CustomizeInputDialog"
        fun newInstance() = CustomizeInputDialogFragment()
    }
}
