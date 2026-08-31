package com.example.algolens.ui.visualizer

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.graphics.Color
import com.example.algolens.ui.theme.AlgoTokens

/**
 * Challenge Mode "TARGET" cell helpers.
 *
 * The "target" cell is the user's goal during a Challenge Mode round (e.g.
 * "find the missing number at this index"). It is rendered with a yellow
 * pulsing halo (driven by [rememberChallengePulseState]) and a thicker
 * border. The pulse is read inside draw lambdas only, so the infinite
 * animation never triggers per-frame recomposition.
 *
 * The pulse state is created once per visualizer and shared across all
 * target cells — do not recreate per cell or each cell starts its own
 * infinite transition.
 */
@Composable
fun rememberChallengePulseState(): State<Float> =
    rememberInfiniteTransition(label = "challengeHalo").animateFloat(
        initialValue = 0.55f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(650), RepeatMode.Reverse),
        label = "challengeHaloAlpha"
    )

/**
 * Cell color triple for a Challenge Mode target cell. Used by the per-cell
 * color resolution in [CellGrid].
 */
internal fun challengeTargetColorTriple(): Triple<Color, Color, Color> =
    Triple(AlgoTokens.accentYellow, AlgoTokens.yellowFill, AlgoTokens.accentYellow)
