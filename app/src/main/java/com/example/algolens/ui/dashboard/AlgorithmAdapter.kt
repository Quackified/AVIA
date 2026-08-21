package com.example.algolens.ui.dashboard

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.algolens.databinding.ItemAlgorithmBinding
import com.example.algolens.model.Algorithm

class AlgorithmAdapter(
    private var algorithms: List<Algorithm>,
    private val onItemClick: (Algorithm) -> Unit
) : RecyclerView.Adapter<AlgorithmAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemAlgorithmBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAlgorithmBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val algo = algorithms[position]
        with(holder.binding) {
            tvAlgoName.text = algo.name
            tvAlgoTime.text = algo.timeComplexity
            tvAlgoDifficulty.text = algo.difficulty

            // Set color based on difficulty
            val diffColor = when (algo.difficulty) {
                "Easy" -> Color.parseColor("#4ADE80")
                "Medium" -> Color.parseColor("#FBBF24")
                "Hard" -> Color.parseColor("#F87171")
                else -> Color.parseColor("#22D3EE")
            }
            tvAlgoDifficulty.setTextColor(diffColor)
            tvAlgoDifficulty.backgroundTintList = ColorStateList.valueOf(diffColor).withAlpha(25)

            // Set category color
            val catColor = Color.parseColor(algo.colorHex)
            layoutIconContainer.backgroundTintList = ColorStateList.valueOf(catColor).withAlpha(24)
            ivAlgoIcon.imageTintList = ColorStateList.valueOf(catColor)

            root.setOnClickListener { onItemClick(algo) }
        }
    }

    override fun getItemCount() = algorithms.size

    fun updateData(newAlgorithms: List<Algorithm>) {
        algorithms = newAlgorithms
        notifyDataSetChanged()
    }
}
