package com.example.algolens.ui.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.algolens.ui.components.AlgoGlyphs
import com.example.algolens.ui.components.AviaLogo
import com.example.algolens.ui.components.DoubleBezelShell
import com.example.algolens.ui.components.pressPhysics
import com.example.algolens.ui.theme.AccentGreen
import com.example.algolens.ui.theme.AccentPink
import com.example.algolens.ui.theme.AccentYellow
import com.example.algolens.ui.theme.AlgoTokens
import com.example.algolens.ui.theme.AlgoType
import com.example.algolens.ui.theme.BorderCyan
import com.example.algolens.ui.theme.BorderSubtle
import com.example.algolens.ui.theme.CanvasBackground
import com.example.algolens.ui.theme.CardBackground
import com.example.algolens.ui.theme.CardBackgroundHover
import com.example.algolens.ui.theme.CyanSubtle
import com.example.algolens.ui.theme.DarkBackground
import com.example.algolens.ui.theme.GreenSubtle
import com.example.algolens.ui.theme.PinkSubtle
import com.example.algolens.ui.theme.PrimaryCyan
import com.example.algolens.ui.theme.PurpleSubtle
import com.example.algolens.ui.theme.SecondaryPurple
import com.example.algolens.ui.theme.TextDark
import com.example.algolens.ui.theme.TextMuted
import com.example.algolens.ui.theme.TextPrimary
import com.example.algolens.ui.theme.TextSecondary
import com.example.algolens.ui.theme.YellowSubtle
import kotlinx.coroutines.launch

/**
 * High-craft tech-noir onboarding tour for first-time AVIA users.
 *
 * 3-slide interactive overview covering:
 *  1. Runtime Invariant Visualizer & Comparison Bridges
 *  2. 100% Offline AI Intelligence & Algorithmic Synthesis
 *  3. Practice & Invariant Challenges
 */
@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CanvasBackground)
            .systemBarsPadding()
            .padding(horizontal = AlgoTokens.space6, vertical = AlgoTokens.space4),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // ── 1. Top Bar: AVIA Mark + Skip Action ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(AlgoTokens.minTouchTarget),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
            ) {
                AviaLogo(
                    size = AlgoTokens.inlineIconLg,
                    enableGlow = true
                )
                Text(
                    text = "AVIA",
                    style = MaterialTheme.typography.titleMedium,
                    color = PrimaryCyan,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = AlgoType.trackSection
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                    .clickable { onComplete() }
                    .padding(horizontal = AlgoTokens.space3, vertical = AlgoTokens.space2),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "SKIP",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontWeight = FontWeight.Bold,
                    fontSize = AlgoType.microSize,
                    letterSpacing = AlgoType.trackSection
                )
            }
        }

        // ── 2. Center Multi-Page Content ──
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) { page ->
            when (page) {
                0 -> OnboardingSlideVisualizer()
                1 -> OnboardingSlideAiTutor()
                2 -> OnboardingSlidePractice()
            }
        }

        // ── 3. Bottom Controls: Instrument Ticks + Action Button ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(AlgoTokens.minTouchTarget)
                .padding(top = AlgoTokens.space2),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Precision Instrument Page Indicators
            Row(
                horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 0 until 3) {
                    val isActive = pagerState.currentPage == i
                    Box(
                        modifier = Modifier
                            .height(AlgoTokens.space2)
                            .width(if (isActive) AlgoTokens.space7 else AlgoTokens.space3)
                            .clip(RoundedCornerShape(AlgoTokens.radiusXxs))
                            .background(if (isActive) PrimaryCyan else BorderSubtle)
                    )
                }
            }

            // Primary Action Button
            val isFinalPage = pagerState.currentPage == 2

            val buttonShape = RoundedCornerShape(AlgoTokens.radiusXs)
            val buttonAccent = PrimaryCyan

            Box(
                modifier = Modifier
                    .pressPhysics(shape = buttonShape, accent = buttonAccent)
                    .clip(buttonShape)
                    .background(if (isFinalPage) PrimaryCyan else CardBackgroundHover)
                    .border(
                        width = AlgoTokens.strokeThin,
                        color = if (isFinalPage) PrimaryCyan else BorderCyan,
                        shape = buttonShape
                    )
                    .clickable {
                        if (isFinalPage) {
                            onComplete()
                        } else {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    }
                    .padding(horizontal = AlgoTokens.space5, vertical = AlgoTokens.space3),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
                ) {
                    Text(
                        text = if (isFinalPage) "INITIALIZE WORKSPACE" else "NEXT",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isFinalPage) DarkBackground else PrimaryCyan,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = AlgoType.trackSection
                    )
                    Icon(
                        imageVector = if (isFinalPage) AlgoGlyphs.Spark else AlgoGlyphs.ChevronRight,
                        contentDescription = null,
                        tint = if (isFinalPage) DarkBackground else PrimaryCyan,
                        modifier = Modifier.size(AlgoTokens.inlineIconMd)
                    )
                }
            }
        }
    }
}

/** Slide 1: Runtime Invariant Visualizer */
@Composable
private fun OnboardingSlideVisualizer() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = AlgoTokens.space4),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        // Hero Graphic: Mini Visualizer Representation
        DoubleBezelShell(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(AlgoTokens.space5)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(AlgoTokens.space4)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SWAP SEQUENCE · BUBBLE SORT",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontSize = AlgoType.microSize,
                        letterSpacing = AlgoType.trackSection
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                            .background(PinkSubtle)
                            .padding(horizontal = AlgoTokens.space2, vertical = AlgoTokens.space1)
                    ) {
                        Text(
                            text = "SWAPPING",
                            style = MaterialTheme.typography.labelSmall,
                            color = AccentPink,
                            fontSize = AlgoType.microSize,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Mini array cells with pointer badges
                Row(
                    horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space3),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MiniCellItem(value = 12, index = 0, state = "IDLE")
                    MiniCellItem(value = 64, index = 1, pointer = "j", state = "SWAP")
                    MiniCellItem(value = 34, index = 2, pointer = "j+1", state = "SWAP")
                    MiniCellItem(value = 90, index = 3, state = "SORTED")
                }
            }
        }

        // Narrative Copy
        Column(verticalArrangement = Arrangement.spacedBy(AlgoTokens.space2)) {
            Text(
                text = "01 // RUNTIME INVARIANTS",
                style = MaterialTheme.typography.labelSmall,
                color = PrimaryCyan,
                fontWeight = FontWeight.Bold,
                letterSpacing = AlgoType.trackBrand
            )
            Text(
                text = "Interactive Algorithm Execution",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Track array states, staple comparison bridges, and memory mutations in real-time. Built with render-phase Jetpack Compose graphics for zero frame drops.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(AlgoTokens.space2))

            Row(
                horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
            ) {
                FeatureChip(label = "13 Algorithms")
                FeatureChip(label = "Comparison Bridges")
                FeatureChip(label = "Divide & Conquer")
            }
        }
    }
}

/** Slide 2: 100% Offline AI Intelligence */
@Composable
private fun OnboardingSlideAiTutor() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = AlgoTokens.space4),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        // Hero Graphic: Mini AI Flowchart / Complexity Card
        DoubleBezelShell(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(AlgoTokens.space5)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(AlgoTokens.space3)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "OFFLINE AI SYNTHESIS",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontSize = AlgoType.microSize,
                        letterSpacing = AlgoType.trackSection
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                            .background(CyanSubtle)
                            .padding(horizontal = AlgoTokens.space2, vertical = AlgoTokens.space1)
                    ) {
                        Text(
                            text = "100% OFFLINE",
                            style = MaterialTheme.typography.labelSmall,
                            color = PrimaryCyan,
                            fontSize = AlgoType.microSize,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Mini Flowchart Nodes Representation
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MiniFlowchartNode(label = "Start", isTerminal = true)
                    Icon(
                        imageVector = AlgoGlyphs.ChevronRight,
                        contentDescription = null,
                        tint = BorderSubtle,
                        modifier = Modifier.size(AlgoTokens.inlineIconSm)
                    )
                    MiniFlowchartNode(label = "arr[j] > arr[j+1]?", isDecision = true)
                    Icon(
                        imageVector = AlgoGlyphs.ChevronRight,
                        contentDescription = null,
                        tint = BorderSubtle,
                        modifier = Modifier.size(AlgoTokens.inlineIconSm)
                    )
                    MiniFlowchartNode(label = "Swap", isProcess = true)
                }

                // Callout tip preview
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                        .background(GreenSubtle)
                        .padding(horizontal = AlgoTokens.space3, vertical = AlgoTokens.space2)
                ) {
                    Text(
                        text = "> [!TIP] Deterministic grammar parser with side-by-side Big-O matrix tables.",
                        style = MaterialTheme.typography.bodySmall,
                        color = AccentGreen,
                        fontSize = AlgoType.microSize
                    )
                }
            }
        }

        // Narrative Copy
        Column(verticalArrangement = Arrangement.spacedBy(AlgoTokens.space2)) {
            Text(
                text = "02 // ON-DEVICE INTELLIGENCE",
                style = MaterialTheme.typography.labelSmall,
                color = SecondaryPurple,
                fontWeight = FontWeight.Bold,
                letterSpacing = AlgoType.trackBrand
            )
            Text(
                text = "Deterministic Algorithmic Tutor",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Directly query theoretical complexities, Big-O limits, and comparative trade-offs. Powered by on-device pattern synthesis with zero cloud calls, zero latency, and complete privacy.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(AlgoTokens.space2))

            Row(
                horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
            ) {
                FeatureChip(label = "Markdown Tables")
                FeatureChip(label = "Flowchart Diagrams")
                FeatureChip(label = "Callouts")
            }
        }
    }
}

/** Slide 3: Practice & Challenge Mode */
@Composable
private fun OnboardingSlidePractice() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = AlgoTokens.space4),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        // Hero Graphic: Mini Frozen Snapshot Challenge Card
        DoubleBezelShell(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(AlgoTokens.space5)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(AlgoTokens.space3)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CHALLENGE · BINARY SEARCH",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontSize = AlgoType.microSize,
                        letterSpacing = AlgoType.trackSection
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                            .background(YellowSubtle)
                            .padding(horizontal = AlgoTokens.space2, vertical = AlgoTokens.space1)
                    ) {
                        Text(
                            text = "STREAK 5 🔥",
                            style = MaterialTheme.typography.labelSmall,
                            color = AccentYellow,
                            fontSize = AlgoType.microSize,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Mini Bar chart snapshot representation
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(AlgoTokens.minTouchTarget + AlgoTokens.space5)
                        .padding(horizontal = AlgoTokens.space2),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    MiniSnapshotBar(fraction = 0.35f, label = "L", isCyan = true)
                    MiniSnapshotBar(fraction = 0.5f)
                    MiniSnapshotBar(fraction = 0.7f, label = "MID", isYellow = true)
                    MiniSnapshotBar(fraction = 0.85f)
                    MiniSnapshotBar(fraction = 1.0f, label = "R", isPurple = true)
                }
            }
        }

        // Narrative Copy
        Column(verticalArrangement = Arrangement.spacedBy(AlgoTokens.space2)) {
            Text(
                text = "03 // INVARIANT MASTERY",
                style = MaterialTheme.typography.labelSmall,
                color = AccentGreen,
                fontWeight = FontWeight.Bold,
                letterSpacing = AlgoType.trackBrand
            )
            Text(
                text = "Explore & Invariant Challenges",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Analyze frozen data snapshots, predict algorithm mutations, build answer streaks, and solidify fundamental computer science intuition.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(AlgoTokens.space2))

            Row(
                horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
            ) {
                FeatureChip(label = "Frozen Snapshots")
                FeatureChip(label = "Streak Score")
                FeatureChip(label = "Step Predictions")
            }
        }
    }
}

// ── Preview Visual Helpers ──

@Composable
private fun MiniCellItem(
    value: Int,
    index: Int,
    pointer: String? = null,
    state: String
) {
    val borderColor = when (state) {
        "SWAP" -> AccentPink
        "SORTED" -> AccentGreen
        else -> BorderSubtle
    }
    val bg = when (state) {
        "SWAP" -> PinkSubtle
        "SORTED" -> GreenSubtle
        else -> CardBackground
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AlgoTokens.space1)
    ) {
        if (pointer != null) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                    .background(CyanSubtle)
                    .padding(horizontal = AlgoTokens.space2, vertical = AlgoTokens.space1)
            ) {
                Text(
                    text = pointer,
                    style = MaterialTheme.typography.labelSmall,
                    color = PrimaryCyan,
                    fontSize = AlgoType.microSize,
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            Spacer(modifier = Modifier.height(AlgoTokens.inlineIconMd))
        }

        Box(
            modifier = Modifier
                .size(AlgoTokens.iconButtonLg)
                .clip(RoundedCornerShape(AlgoTokens.radiusXxs))
                .background(bg)
                .border(AlgoTokens.strokeThin, borderColor, RoundedCornerShape(AlgoTokens.radiusXxs)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = if (state == "SWAP") AccentPink else TextPrimary,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = index.toString(),
            style = MaterialTheme.typography.labelSmall,
            color = TextDark,
            fontSize = AlgoType.microSize
        )
    }
}

@Composable
private fun MiniFlowchartNode(
    label: String,
    isTerminal: Boolean = false,
    isDecision: Boolean = false,
    isProcess: Boolean = false
) {
    val border = when {
        isDecision -> AccentYellow
        isTerminal -> PrimaryCyan
        else -> SecondaryPurple
    }
    val bg = when {
        isDecision -> YellowSubtle
        isTerminal -> CyanSubtle
        else -> PurpleSubtle
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(AlgoTokens.radiusXs))
            .background(bg)
            .border(AlgoTokens.strokeThin, border, RoundedCornerShape(AlgoTokens.radiusXs))
            .padding(horizontal = AlgoTokens.space2, vertical = AlgoTokens.space2),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = border,
            fontWeight = FontWeight.Bold,
            fontSize = AlgoType.microSize
        )
    }
}

@Composable
private fun MiniSnapshotBar(
    fraction: Float,
    label: String? = null,
    isCyan: Boolean = false,
    isPurple: Boolean = false,
    isYellow: Boolean = false
) {
    val color = when {
        isCyan -> PrimaryCyan
        isPurple -> SecondaryPurple
        isYellow -> AccentYellow
        else -> CardBackgroundHover
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        if (label != null) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = color,
                fontWeight = FontWeight.Bold,
                fontSize = AlgoType.microSize
            )
        }
        Box(
            modifier = Modifier
                .width(AlgoTokens.space6)
                .height((AlgoTokens.space8 + AlgoTokens.space4) * fraction)
                .clip(RoundedCornerShape(topStart = AlgoTokens.radiusXs, topEnd = AlgoTokens.radiusXs))
                .background(color)
        )
    }
}

@Composable
private fun FeatureChip(label: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(AlgoTokens.radiusXs))
            .background(CardBackground)
            .border(AlgoTokens.strokeThin, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusXs))
            .padding(horizontal = AlgoTokens.space3, vertical = AlgoTokens.space1)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            fontSize = AlgoType.microSize
        )
    }
}
