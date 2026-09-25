package com.example.algolens.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathBuilder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/**
 * ============================================================================
 *  AlgoGlyphs — bespoke stroke-1.5 instrument icon set (M3)
 * ============================================================================
 *  Hand-authored Lucide-style geometry: 24dp grid, 1.5dp stroke, round caps
 *  and joins, **no fills**. Zero Material icons remain in app UI; the only
 *  exception is OS-contract art (launcher / splash drawables).
 *
 *  Stroke colour is white and is *always* overridden by the `Icon(tint = …)`
 *  call site — an AlgoGlyphs vector must never be rendered untinted.
 *
 *  Sizes: 16dp inline, 20dp standard, 24dp rail. Every tappable glyph keeps a
 *  sentence-case `contentDescription` (UI_GUIDELINES §13).
 */
object AlgoGlyphs {

    private const val STROKE = 1.5f

    private fun glyph(name: String, pathBuilder: PathBuilder.() -> Unit): ImageVector =
        ImageVector.Builder(
            name = name,
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).path(
            fill = null,
            stroke = SolidColor(Color.White),
            strokeLineWidth = STROKE,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
            pathBuilder = pathBuilder
        ).build()

    /** Circle drawn as two half-arcs so it stays a pure stroke, never a fill. */
    private fun PathBuilder.circle(cx: Float, cy: Float, r: Float) {
        moveTo(cx - r, cy)
        arcToRelative(r, r, 0f, true, true, 2f * r, 0f)
        arcToRelative(r, r, 0f, true, true, -2f * r, 0f)
        close()
    }

    private fun PathBuilder.roundRect(left: Float, top: Float, right: Float, bottom: Float, r: Float) {
        moveTo(left + r, top)
        lineTo(right - r, top)
        quadTo(right, top, right, top + r)
        lineTo(right, bottom - r)
        quadTo(right, bottom, right - r, bottom)
        lineTo(left + r, bottom)
        quadTo(left, bottom, left, bottom - r)
        lineTo(left, top + r)
        quadTo(left, top, left + r, top)
        close()
    }

    private fun PathBuilder.line(x1: Float, y1: Float, x2: Float, y2: Float) {
        moveTo(x1, y1)
        lineTo(x2, y2)
    }

    // ── Playback transport ──────────────────────────────────────────────
    val Play: ImageVector = glyph("AlgoGlyphs.Play") {
        moveTo(8f, 5f); lineTo(19f, 12f); lineTo(8f, 19f); close()
    }

    val PlayCircle: ImageVector = glyph("AlgoGlyphs.PlayCircle") {
        circle(12f, 12f, 9f)
        moveTo(10f, 8.5f); lineTo(16f, 12f); lineTo(10f, 15.5f); close()
    }

    val Pause: ImageVector = glyph("AlgoGlyphs.Pause") {
        line(9.5f, 5f, 9.5f, 19f)
        line(14.5f, 5f, 14.5f, 19f)
    }

    val StepBack: ImageVector = glyph("AlgoGlyphs.StepBack") {
        moveTo(18.5f, 5.5f); lineTo(8.5f, 12f); lineTo(18.5f, 18.5f); close()
        line(5.5f, 5f, 5.5f, 19f)
    }

    val StepForward: ImageVector = glyph("AlgoGlyphs.StepForward") {
        moveTo(5.5f, 5.5f); lineTo(15.5f, 12f); lineTo(5.5f, 18.5f); close()
        line(18.5f, 5f, 18.5f, 19f)
    }

    val Reset: ImageVector = glyph("AlgoGlyphs.Reset") {
        moveTo(3.5f, 12f)
        arcToRelative(8.5f, 8.5f, 0f, true, false, 8.5f, -8.5f)
        quadTo(6.8f, 3.5f, 3.5f, 8.5f)
        moveTo(3.5f, 3.5f)
        lineTo(3.5f, 8.5f)
        lineTo(8.5f, 8.5f)
    }

    val Refresh: ImageVector = glyph("AlgoGlyphs.Refresh") {
        moveTo(3.5f, 12f)
        arcToRelative(8.5f, 8.5f, 0f, true, false, 8.5f, -8.5f)
        quadTo(6.8f, 3.5f, 3.5f, 8.5f)
        moveTo(3.5f, 3.5f)
        lineTo(3.5f, 8.5f)
        lineTo(8.5f, 8.5f)
    }

    val Speed: ImageVector = glyph("AlgoGlyphs.Speed") {
        circle(12f, 12f, 8.5f)
        line(12f, 12f, 16.5f, 8f)
        circle(12f, 12f, 1.2f)
    }

    val Shuffle: ImageVector = glyph("AlgoGlyphs.Shuffle") {
        moveTo(3.5f, 7f); lineTo(8f, 7f); lineTo(16f, 17f); lineTo(20.5f, 17f)
        moveTo(17f, 13.5f); lineTo(20.5f, 17f); lineTo(17f, 20.5f)
        moveTo(3.5f, 17f); lineTo(8f, 17f); lineTo(10.5f, 14f)
        moveTo(14f, 10f); lineTo(16f, 7f); lineTo(20.5f, 7f)
        moveTo(17f, 3.5f); lineTo(20.5f, 7f); lineTo(17f, 10.5f)
    }

    // ── Navigation ──────────────────────────────────────────────────────
    val Home: ImageVector = glyph("AlgoGlyphs.Home") {
        moveTo(3.5f, 10.5f); lineTo(12f, 3.5f); lineTo(20.5f, 10.5f)
        line(20.5f, 10.5f, 20.5f, 20f); line(20.5f, 20f, 3.5f, 20f); line(3.5f, 20f, 3.5f, 10.5f)
        moveTo(9.5f, 20f); lineTo(9.5f, 14.5f); lineTo(14.5f, 14.5f); lineTo(14.5f, 20f)
    }

    val Compass: ImageVector = glyph("AlgoGlyphs.Compass") {
        circle(12f, 12f, 8.5f)
        moveTo(16.2f, 7.8f); lineTo(14.1f, 14.1f); lineTo(7.8f, 16.2f); lineTo(9.9f, 9.9f); close()
    }

    val Person: ImageVector = glyph("AlgoGlyphs.Person") {
        circle(12f, 8f, 3.5f)
        moveTo(5f, 20.5f)
        quadTo(5f, 14f, 12f, 14f)
        quadTo(19f, 14f, 19f, 20.5f)
    }

    val Back: ImageVector = glyph("AlgoGlyphs.Back") {
        line(19.5f, 12f, 4.5f, 12f)
        moveTo(11f, 5.5f); lineTo(4.5f, 12f); lineTo(11f, 18.5f)
    }

    val ChevronRight: ImageVector = glyph("AlgoGlyphs.ChevronRight") {
        moveTo(9.5f, 5f); lineTo(16.5f, 12f); lineTo(9.5f, 19f)
    }

    val ChevronDown: ImageVector = glyph("AlgoGlyphs.ChevronDown") {
        moveTo(6f, 9.5f); lineTo(12f, 15.5f); lineTo(18f, 9.5f)
    }

    val ArrowDown: ImageVector = glyph("AlgoGlyphs.ArrowDown") {
        line(12f, 4f, 12f, 20f)
        moveTo(6f, 14f); lineTo(12f, 20f); lineTo(18f, 14f)
    }

    val ArrowUp: ImageVector = glyph("AlgoGlyphs.ArrowUp") {
        line(12f, 20f, 12f, 4f)
        moveTo(6f, 10f); lineTo(12f, 4f); lineTo(18f, 10f)
    }

    val Expand: ImageVector = glyph("AlgoGlyphs.Expand") {
        moveTo(4.5f, 9.5f); lineTo(4.5f, 4.5f); lineTo(9.5f, 4.5f)
        moveTo(14.5f, 4.5f); lineTo(19.5f, 4.5f); lineTo(19.5f, 9.5f)
        moveTo(19.5f, 14.5f); lineTo(19.5f, 19.5f); lineTo(14.5f, 19.5f)
        moveTo(9.5f, 19.5f); lineTo(4.5f, 19.5f); lineTo(4.5f, 14.5f)
    }

    val Close: ImageVector = glyph("AlgoGlyphs.Close") {
        line(6f, 6f, 18f, 18f)
        line(18f, 6f, 6f, 18f)
    }

    val Check: ImageVector = glyph("AlgoGlyphs.Check") {
        moveTo(4.5f, 12.5f); lineTo(9.5f, 17.5f); lineTo(19.5f, 6.5f)
    }

    val CheckCircle: ImageVector = glyph("AlgoGlyphs.CheckCircle") {
        circle(12f, 12f, 8.5f)
        moveTo(8f, 12.3f); lineTo(11f, 15.3f); lineTo(16f, 9f)
    }

    val Plus: ImageVector = glyph("AlgoGlyphs.Plus") {
        line(12f, 4.5f, 12f, 19.5f)
        line(4.5f, 12f, 19.5f, 12f)
    }

    val More: ImageVector = glyph("AlgoGlyphs.More") {
        circle(12f, 5f, 1.4f)
        circle(12f, 12f, 1.4f)
        circle(12f, 19f, 1.4f)
    }

    val SwapVert: ImageVector = glyph("AlgoGlyphs.SwapVert") {
        line(8f, 20f, 8f, 4.5f)
        moveTo(4.5f, 8f); lineTo(8f, 4.5f); lineTo(11.5f, 8f)
        line(16f, 4f, 16f, 19.5f)
        moveTo(12.5f, 16f); lineTo(16f, 19.5f); lineTo(19.5f, 16f)
    }

    val SwapHoriz: ImageVector = glyph("AlgoGlyphs.SwapHoriz") {
        line(20f, 8f, 4.5f, 8f)
        moveTo(8f, 4.5f); lineTo(4.5f, 8f); lineTo(8f, 11.5f)
        line(4f, 16f, 19.5f, 16f)
        moveTo(16f, 12.5f); lineTo(19.5f, 16f); lineTo(16f, 19.5f)
    }

    val Trash: ImageVector = glyph("AlgoGlyphs.Trash") {
        line(4f, 6.5f, 20f, 6.5f)
        moveTo(9.5f, 6.5f); lineTo(9.5f, 4f); lineTo(14.5f, 4f); lineTo(14.5f, 6.5f)
        moveTo(6.5f, 6.5f); lineTo(7.5f, 20f); lineTo(16.5f, 20f); lineTo(17.5f, 6.5f)
        line(10.5f, 10.5f, 10.5f, 16f)
        line(13.5f, 10.5f, 13.5f, 16f)
    }

    // ── Domain / content glyphs ─────────────────────────────────────────
    val Grid: ImageVector = glyph("AlgoGlyphs.Grid") {
        roundRect(3.5f, 3.5f, 10.5f, 10.5f, 1.5f)
        roundRect(13.5f, 3.5f, 20.5f, 10.5f, 1.5f)
        roundRect(3.5f, 13.5f, 10.5f, 20.5f, 1.5f)
        roundRect(13.5f, 13.5f, 20.5f, 20.5f, 1.5f)
    }

    val Layers: ImageVector = glyph("AlgoGlyphs.Layers") {
        moveTo(12f, 3f); lineTo(21f, 8f); lineTo(12f, 13f); lineTo(3f, 8f); close()
        moveTo(21f, 12.5f); lineTo(12f, 17.5f); lineTo(3f, 12.5f)
        moveTo(19f, 16.5f); lineTo(12f, 20.5f); lineTo(5f, 16.5f)
    }

    val Tree: ImageVector = glyph("AlgoGlyphs.Tree") {
        roundRect(9f, 3f, 15f, 7.5f, 1.5f)
        roundRect(2.5f, 16.5f, 8.5f, 21f, 1.5f)
        roundRect(15.5f, 16.5f, 21.5f, 21f, 1.5f)
        line(12f, 7.5f, 12f, 12f)
        moveTo(5.5f, 16.5f); lineTo(5.5f, 12f); lineTo(18.5f, 12f); lineTo(18.5f, 16.5f)
    }

    val Nodes: ImageVector = glyph("AlgoGlyphs.Nodes") {
        circle(12f, 5.5f, 2.4f)
        circle(5.5f, 18f, 2.4f)
        circle(18.5f, 18f, 2.4f)
        line(11f, 7.6f, 6.5f, 15.8f)
        line(13f, 7.6f, 17.5f, 15.8f)
        line(8f, 18f, 16f, 18f)
    }

    val Stack: ImageVector = glyph("AlgoGlyphs.Stack") {
        roundRect(3.5f, 4f, 20.5f, 8f, 1.5f)
        roundRect(3.5f, 10f, 20.5f, 14f, 1.5f)
        roundRect(3.5f, 16f, 20.5f, 20f, 1.5f)
    }

    val Queue: ImageVector = glyph("AlgoGlyphs.Queue") {
        roundRect(2.5f, 7.5f, 9.5f, 16.5f, 1.5f)
        roundRect(11f, 7.5f, 18f, 16.5f, 1.5f)
        line(19.5f, 12f, 22f, 12f)
        moveTo(20f, 9.5f); lineTo(22.5f, 12f); lineTo(20f, 14.5f)
    }

    val Terminal: ImageVector = glyph("AlgoGlyphs.Terminal") {
        roundRect(2.5f, 4f, 21.5f, 20f, 2.5f)
        moveTo(6.5f, 9.5f); lineTo(9.5f, 12.5f); lineTo(6.5f, 15.5f)
        line(12f, 15.5f, 17f, 15.5f)
    }

    val Code: ImageVector = glyph("AlgoGlyphs.Code") {
        moveTo(9f, 7f); lineTo(4f, 12f); lineTo(9f, 17f)
        moveTo(15f, 7f); lineTo(20f, 12f); lineTo(15f, 17f)
        line(13.5f, 4.5f, 10.5f, 19.5f)
    }

    val Search: ImageVector = glyph("AlgoGlyphs.Search") {
        circle(10.5f, 10.5f, 6.5f)
        line(15.2f, 15.2f, 20.5f, 20.5f)
    }

    val ChevronUp: ImageVector = glyph("AlgoGlyphs.ChevronUp") {
        moveTo(6f, 14.5f); lineTo(12f, 8.5f); lineTo(18f, 14.5f)
    }

    val Tune: ImageVector = glyph("AlgoGlyphs.Tune") {
        line(3.5f, 6.5f, 12.3f, 6.5f)
        circle(14.5f, 6.5f, 2.1f)
        line(16.7f, 6.5f, 20.5f, 6.5f)

        line(3.5f, 12f, 6.3f, 12f)
        circle(8.5f, 12f, 2.1f)
        line(10.7f, 12f, 20.5f, 12f)

        line(3.5f, 17.5f, 14.3f, 17.5f)
        circle(16.5f, 17.5f, 2.1f)
        line(18.7f, 17.5f, 20.5f, 17.5f)
    }

    val Sliders: ImageVector = glyph("AlgoGlyphs.Sliders") {
        // Left slider track + hollow orb
        line(6f, 4f, 6f, 12.9f)
        circle(6f, 15f, 2.1f)
        line(6f, 17.1f, 6f, 20f)

        // Middle slider track + hollow orb
        line(12f, 4f, 12f, 6.4f)
        circle(12f, 8.5f, 2.1f)
        line(12f, 10.6f, 12f, 20f)

        // Right slider track + hollow orb
        line(18f, 4f, 18f, 13.4f)
        circle(18f, 15.5f, 2.1f)
        line(18f, 17.6f, 18f, 20f)
    }

    val Book: ImageVector = glyph("AlgoGlyphs.Book") {
        line(12f, 7f, 12f, 20f)
        moveTo(3.5f, 4.5f); lineTo(3.5f, 17.5f); quadTo(8f, 17.5f, 12f, 20f)
        moveTo(3.5f, 4.5f); quadTo(8f, 4.5f, 12f, 7f)
        moveTo(20.5f, 4.5f); lineTo(20.5f, 17.5f); quadTo(16f, 17.5f, 12f, 20f)
        moveTo(20.5f, 4.5f); quadTo(16f, 4.5f, 12f, 7f)
    }

    val Bookmark: ImageVector = glyph("AlgoGlyphs.Bookmark") {
        moveTo(6f, 3.5f); lineTo(18f, 3.5f); lineTo(18f, 20.5f)
        lineTo(12f, 15.5f); lineTo(6f, 20.5f); close()
    }

    val BookmarkFilled: ImageVector = glyph("AlgoGlyphs.BookmarkFilled") {
        moveTo(6f, 3.5f); lineTo(18f, 3.5f); lineTo(18f, 20.5f)
        lineTo(12f, 15.5f); lineTo(6f, 20.5f); close()
        // Inner checkmark indicator for saved state
        moveTo(9.5f, 9.8f); lineTo(11.3f, 11.6f); lineTo(14.8f, 8.1f)
    }

    val Bolt: ImageVector = glyph("AlgoGlyphs.Bolt") {
        moveTo(13.5f, 2.5f); lineTo(6f, 13.5f); lineTo(11.5f, 13.5f)
        lineTo(10.5f, 21.5f); lineTo(18f, 10.5f); lineTo(12.5f, 10.5f); close()
    }

    val Avia: ImageVector = glyph("AlgoGlyphs.Avia") {
        // Outer chevron "A"
        moveTo(5.5f, 19.5f); lineTo(12f, 4.5f); lineTo(18.5f, 19.5f)
        // Baseline anchor feet
        line(4f, 19.5f, 7f, 19.5f)
        line(17f, 19.5f, 20f, 19.5f)
        // Comparison bridge staple crossbar |—|
        moveTo(8f, 13f); lineTo(8f, 15f); lineTo(16f, 15f); lineTo(16f, 13f)
        // Central aperture diamond core
        moveTo(12f, 8f); lineTo(14f, 10.5f); lineTo(12f, 13f); lineTo(10f, 10.5f); close()
    }

    val Target: ImageVector = glyph("AlgoGlyphs.Target") {
        circle(12f, 12f, 8f)
        circle(12f, 12f, 3.5f)
        line(12f, 1.5f, 12f, 3.5f)
        line(12f, 20.5f, 12f, 22.5f)
        line(1.5f, 12f, 3.5f, 12f)
        line(20.5f, 12f, 22.5f, 12f)
    }

    val Spark: ImageVector = glyph("AlgoGlyphs.Spark") {
        moveTo(11f, 3f)
        quadTo(12.4f, 9.6f, 19f, 11f)
        quadTo(12.4f, 12.4f, 11f, 19f)
        quadTo(9.6f, 12.4f, 3f, 11f)
        quadTo(9.6f, 9.6f, 11f, 3f)
        close()
        line(18.5f, 16f, 18.5f, 21f)
        line(16f, 18.5f, 21f, 18.5f)
    }

    val Offline: ImageVector = glyph("AlgoGlyphs.Offline") {
        line(3.5f, 18f, 3.5f, 15f)
        line(8f, 18f, 8f, 11.5f)
        line(12.5f, 18f, 12.5f, 8f)
        line(17f, 18f, 17f, 4.5f)
        line(3f, 3f, 21f, 21f)
    }

    val Storage: ImageVector = glyph("AlgoGlyphs.Storage") {
        moveTo(3.5f, 7f)
        quadTo(3.5f, 3.5f, 12f, 3.5f)
        quadTo(20.5f, 3.5f, 20.5f, 7f)
        lineTo(20.5f, 17f)
        quadTo(20.5f, 20.5f, 12f, 20.5f)
        quadTo(3.5f, 20.5f, 3.5f, 17f)
        close()
        moveTo(3.5f, 12f)
        quadTo(3.5f, 15.5f, 12f, 15.5f)
        quadTo(20.5f, 15.5f, 20.5f, 12f)
    }

    // ── System / status glyphs ──────────────────────────────────────────
    val Alert: ImageVector = glyph("AlgoGlyphs.Alert") {
        circle(12f, 12f, 8.5f)
        line(12f, 7.5f, 12f, 13.5f)
        circle(12f, 16.6f, 0.8f)
    }

    val Warning: ImageVector = glyph("AlgoGlyphs.Warning") {
        moveTo(12f, 3.5f); lineTo(21.5f, 20f); lineTo(2.5f, 20f); close()
        line(12f, 9.5f, 12f, 14.5f)
        circle(12f, 17.2f, 0.8f)
    }

    val Info: ImageVector = glyph("AlgoGlyphs.Info") {
        circle(12f, 12f, 8.5f)
        line(12f, 11f, 12f, 16.5f)
        circle(12f, 7.8f, 0.8f)
    }

    val Help: ImageVector = glyph("AlgoGlyphs.Help") {
        circle(12f, 12f, 8.5f)
        moveTo(9.5f, 9.5f)
        quadTo(9.5f, 5.5f, 13.5f, 6.5f)
        quadTo(16f, 7.2f, 14.5f, 10.5f)
        quadTo(13f, 12.5f, 12f, 13f)
        line(12f, 15.5f, 12f, 15.7f)
        circle(12f, 18f, 0.8f)
    }

    val Tap: ImageVector = glyph("AlgoGlyphs.Tap") {
        circle(12f, 12f, 8.5f)
        circle(12f, 12f, 2f)
    }

    val Bars: ImageVector = glyph("AlgoGlyphs.Bars") {
        line(3f, 20f, 21f, 20f)
        roundRect(4.5f, 12f, 8.5f, 19f, 1.2f)
        roundRect(10f, 7f, 14f, 19f, 1.2f)
        roundRect(15.5f, 3.5f, 19.5f, 19f, 1.2f)
    }

    val ListOrdered: ImageVector = glyph("AlgoGlyphs.ListOrdered") {
        line(9.5f, 6.5f, 20.5f, 6.5f)
        line(9.5f, 12f, 20.5f, 12f)
        line(9.5f, 17.5f, 20.5f, 17.5f)
        circle(4.5f, 6.5f, 1.6f)
        circle(4.5f, 12f, 1.6f)
        circle(4.5f, 17.5f, 1.6f)
    }

    val TrendingUp: ImageVector = glyph("AlgoGlyphs.TrendingUp") {
        moveTo(22f, 7f); lineTo(13.5f, 15.5f); lineTo(8.5f, 10.5f); lineTo(2f, 17f)
        moveTo(16f, 7f); lineTo(22f, 7f); lineTo(22f, 13f)
    }

    val TrendingDown: ImageVector = glyph("AlgoGlyphs.TrendingDown") {
        moveTo(22f, 17f); lineTo(13.5f, 8.5f); lineTo(8.5f, 13.5f); lineTo(2f, 7f)
        moveTo(16f, 17f); lineTo(22f, 17f); lineTo(22f, 11f)
    }

    val Trophy: ImageVector = glyph("AlgoGlyphs.Trophy") {
        moveTo(6f, 9f); lineTo(6f, 4f); lineTo(18f, 4f); lineTo(18f, 9f)
        arcToRelative(6f, 6f, 0f, false, true, -12f, 0f)
        line(12f, 15f, 12f, 19f)
        line(8f, 19f, 16f, 19f)
        moveTo(6f, 6f); lineTo(3f, 6f); arcToRelative(3f, 3f, 0f, false, false, 3f, 3f)
        moveTo(18f, 6f); lineTo(21f, 6f); arcToRelative(3f, 3f, 0f, false, true, -3f, 3f)
    }

    val Flame: ImageVector = glyph("AlgoGlyphs.Flame") {
        moveTo(8.5f, 14.5f)
        arcToRelative(3.5f, 3.5f, 0f, false, false, 7f, 0f)
        curveToRelative(0f, -3.5f, -3.5f, -5f, -3.5f, -9.5f)
        curveToRelative(-1f, 2.5f, -3.5f, 4f, -3.5f, 9.5f)
        close()
    }

    val Gesture: ImageVector = glyph("AlgoGlyphs.Gesture") {
        moveTo(4f, 14f)
        curveToRelative(2f, -4f, 6f, -4f, 8f, 0f)
        curveToRelative(2f, 4f, 6f, 4f, 8f, 0f)
        moveTo(18f, 11f); lineTo(20f, 14f); lineTo(17f, 16f)
    }

    val Forward: ImageVector = glyph("AlgoGlyphs.Forward") {
        line(5f, 12f, 19f, 12f)
        moveTo(13f, 6f); lineTo(19f, 12f); lineTo(13f, 18f)
    }

    val Lightbulb: ImageVector = glyph("AlgoGlyphs.Lightbulb") {
        moveTo(9f, 18f); lineTo(15f, 18f)
        moveTo(10f, 21f); lineTo(14f, 21f)
        moveTo(9f, 15f)
        arcToRelative(6f, 6f, 0f, true, true, 6f, 0f)
        lineTo(15f, 18f); lineTo(9f, 18f); close()
    }

    val Chat: ImageVector = glyph("AlgoGlyphs.Chat") {
        roundRect(3.5f, 4f, 20.5f, 17f, 3f)
        moveTo(8f, 17f); lineTo(5.5f, 20.5f); lineTo(11f, 17f)
    }

    val Send: ImageVector = glyph("AlgoGlyphs.Send") {
        moveTo(21f, 3f); lineTo(10f, 14f)
        moveTo(21f, 3f); lineTo(14.5f, 21f); lineTo(10f, 14f); lineTo(3f, 9.5f); close()
    }

    val Copy: ImageVector = glyph("AlgoGlyphs.Copy") {
        roundRect(8f, 8f, 20.5f, 20.5f, 2f)
        moveTo(4f, 16f); lineTo(4f, 4.5f)
        quadTo(4f, 4f, 4.5f, 4f)
        lineTo(16f, 4f)
    }

    /** Actual toothed gear icon for Settings. */
    val SettingsGear: ImageVector = glyph("AlgoGlyphs.SettingsGear") {
        circle(12f, 12f, 3.2f)
        circle(12f, 12f, 6.8f)
        line(12f, 2.5f, 12f, 5.2f)
        line(12f, 18.8f, 12f, 21.5f)
        line(2.5f, 12f, 5.2f, 12f)
        line(18.8f, 12f, 21.5f, 12f)
        line(5.3f, 5.3f, 7.2f, 7.2f)
        line(16.8f, 16.8f, 18.7f, 18.7f)
        line(18.7f, 5.3f, 16.8f, 7.2f)
        line(7.2f, 16.8f, 5.3f, 18.7f)
    }

    /** Pencil icon for editing profile and renaming chat threads. */
    val EditPencil: ImageVector = glyph("AlgoGlyphs.EditPencil") {
        moveTo(16.5f, 3.5f)
        lineTo(20.5f, 7.5f)
        lineTo(8f, 20f)
        lineTo(3.5f, 20.5f)
        lineTo(4f, 16f)
        close()
        line(14f, 6f, 18f, 10f)
    }

    /** Sidebar / Hamburger drawer icon for Chat History Sidebar. */
    val SidebarMenu: ImageVector = glyph("AlgoGlyphs.SidebarMenu") {
        roundRect(3f, 4f, 21f, 20f, 2.5f)
        line(9f, 4f, 9f, 20f)
        line(12.5f, 9f, 17.5f, 9f)
        line(12.5f, 13f, 17.5f, 13f)
    }
}

