package com.example.algolens.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.algolens.R
import com.example.algolens.data.SampleData
import com.example.algolens.databinding.FragmentProfileBinding
import com.example.algolens.ui.dashboard.AlgorithmAdapter
import com.example.algolens.ui.visualizer.VisualizerFragment

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupActivityChart()
        setupBookmarkedList()
    }

    private fun setupActivityChart() {
        val weeklyActivity = listOf(3, 5, 2, 7, 4, 6, 3)
        val days = listOf("M", "T", "W", "T", "F", "S", "S")
        
        binding.layoutActivityBars.removeAllViews()
        
        weeklyActivity.forEachIndexed { index, value ->
            val barContainer = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                gravity = android.view.Gravity.BOTTOM or android.view.Gravity.CENTER_HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)
            }
            
            // The actual bar
            val bar = View(requireContext()).apply {
                val heightPx = (value / 7f * 40 * resources.displayMetrics.density).toInt()
                layoutParams = LinearLayout.LayoutParams(
                    (12 * resources.displayMetrics.density).toInt(),
                    heightPx
                )
                val isToday = index == 6
                setBackgroundResource(R.drawable.chip_background_selector)
                backgroundTintList = android.content.res.ColorStateList.valueOf(
                    ContextCompat.getColor(requireContext(), if (isToday) R.color.primary_cyan else R.color.bg_card)
                ).withAlpha(if (isToday) 255 else 100)
            }
            
            // Day label
            val label = android.widget.TextView(requireContext()).apply {
                text = days[index]
                textSize = 8f
                gravity = android.view.Gravity.CENTER
                setTextColor(ContextCompat.getColor(requireContext(), if (index == 6) R.color.primary_cyan else R.color.text_muted))
                setPadding(0, 4, 0, 0)
            }
            
            barContainer.addView(bar)
            barContainer.addView(label)
            binding.layoutActivityBars.addView(barContainer)
        }
    }

    private fun setupBookmarkedList() {
        val bookmarked = SampleData.algorithms.filter { it.id in listOf(2, 5, 6) }
        val adapter = AlgorithmAdapter(bookmarked) { algo ->
            val fragment = VisualizerFragment.newInstance(algo.id, algo.name)
            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, fragment)
                .addToBackStack(null)
                .commit()
        }
        
        binding.rvBookmarked.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBookmarked.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
