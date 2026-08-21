package com.example.algolens.ui.visualizer

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.example.algolens.R

/**
 * A custom View that draws an array of numbers as a bar chart.
 * This is used to visualize sorting algorithms.
 */
class BarVisualizerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var data: List<Int> = emptyList()
    private val highlights = mutableMapOf<Int, Int>() // Index -> Color

    private val barPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    private val textPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#64748B")
        textSize = 24f
        textAlign = Paint.Align.CENTER
    }

    private val defaultBarColor = Color.parseColor("#1A2E50")

    /**
     * Updates the data to be displayed and redraws the view.
     */
    fun setData(newData: List<Int>) {
        this.data = newData
        invalidate() // Tells Android to call onDraw again
    }

    /**
     * Sets highlights for specific bars (e.g., comparison or swap).
     */
    fun setHighlights(newHighlights: Map<Int, Int>) {
        this.highlights.clear()
        this.highlights.putAll(newHighlights)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (data.isEmpty()) return

        val maxVal = data.maxOrNull() ?: 1
        val itemCount = data.size
        
        // Calculate dimensions
        val w = width.toFloat()
        val h = height.toFloat()
        val spacing = 8f
        val barWidth = (w - (spacing * (itemCount + 1))) / itemCount
        val maxBarHeight = h - 60f // Leave space for text at the bottom

        for (i in data.indices) {
            val value = data[i]
            val left = spacing + i * (barWidth + spacing)
            val barHeight = (value.toFloat() / maxVal) * maxBarHeight
            val top = maxBarHeight - barHeight
            val right = left + barWidth
            val bottom = maxBarHeight

            // Choose color: check highlights first, then use default
            barPaint.color = highlights[i] ?: defaultBarColor

            // Draw the bar (rounded rectangle)
            val rect = RectF(left, top, right, bottom)
            canvas.drawRoundRect(rect, 8f, 8f, barPaint)

            // Draw the value text below the bar
            canvas.drawText(value.toString(), left + barWidth / 2f, h - 10f, textPaint)
        }
    }
}
