package com.example.algolens.ui.dashboard

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.algolens.R
import com.example.algolens.data.SampleData
import com.example.algolens.databinding.FragmentDashboardBinding
import com.example.algolens.model.Algorithm
import com.example.algolens.ui.visualizer.VisualizerFragment

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: AlgorithmAdapter
    private var currentCategory = "All"
    private var searchQuery = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupCategories()
        setupSearch()
    }

    private fun setupRecyclerView() {
        adapter = AlgorithmAdapter(SampleData.algorithms) { algo ->
            val fragment = VisualizerFragment.newInstance(algo.id, algo.name)
            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, fragment)
                .addToBackStack(null)
                .commit()
        }
        binding.rvAlgorithms.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAlgorithms.adapter = adapter
    }

    private fun setupCategories() {
        SampleData.categories.forEach { category ->
            val button = Button(requireContext()).apply {
                text = category
                textSize = 10f
                isAllCaps = false
                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                layoutParams.setMargins(0, 0, 8, 0)
                this.layoutParams = layoutParams
                
                // Set initial style
                updateCategoryButtonStyle(this, category == currentCategory)

                setOnClickListener {
                    currentCategory = category
                    updateCategoryFilters()
                    filterAlgorithms()
                }
            }
            binding.layoutCategories.addView(button)
        }
    }

    private fun updateCategoryButtonStyle(button: Button, isSelected: Boolean) {
        if (isSelected) {
            button.setBackgroundResource(R.drawable.chip_background_selector)
            button.isSelected = true
            button.setTextColor(ContextCompat.getColor(requireContext(), R.color.bg_dark))
        } else {
            button.setBackgroundResource(R.drawable.chip_background_selector)
            button.isSelected = false
            button.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary))
        }
    }

    private fun updateCategoryFilters() {
        for (i in 0 until binding.layoutCategories.childCount) {
            val child = binding.layoutCategories.getChildAt(i) as? Button
            child?.let { updateCategoryButtonStyle(it, it.text == currentCategory) }
        }
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchQuery = s.toString()
                filterAlgorithms()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterAlgorithms() {
        val filtered = SampleData.algorithms.filter { algo ->
            (currentCategory == "All" || algo.category == currentCategory || (currentCategory == "DP" && algo.category == "Dynamic Programming")) &&
            (searchQuery.isEmpty() || algo.name.contains(searchQuery, ignoreCase = true))
        }
        adapter.updateData(filtered)
        
        binding.tvResultsCount.text = "${filtered.size} RESULTS"
        binding.tvAlgoCountHeader.text = "${filtered.size} algorithms available"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
