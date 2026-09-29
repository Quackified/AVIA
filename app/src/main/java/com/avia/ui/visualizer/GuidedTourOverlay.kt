package com.avia.ui.visualizer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avia.ui.theme.AccentGreen
import com.avia.ui.theme.AccentOrange
import com.avia.ui.theme.AccentPink
import com.avia.ui.theme.AccentYellow
import com.avia.ui.theme.AlgoLensTheme
import com.avia.ui.theme.AlgoTokens
import com.avia.ui.theme.BorderCyan
import com.avia.ui.theme.BorderSubtle
import com.avia.ui.theme.CanvasBackground
import com.avia.ui.theme.CardBackground
import com.avia.ui.theme.CardBackgroundElevated
import com.avia.ui.theme.CyanGlow
import com.avia.ui.theme.CyanSubtle
import com.avia.ui.theme.DarkBackground
import com.avia.ui.theme.GreenSubtle
import com.avia.ui.theme.PinkSubtle
import com.avia.ui.theme.PrimaryCyan
import com.avia.ui.theme.PurpleGlow
import com.avia.ui.theme.PurpleSubtle
import com.avia.ui.theme.SecondaryPurple
import com.avia.ui.theme.TextDark
import com.avia.ui.theme.TextMuted
import com.avia.ui.theme.TextPrimary
import com.avia.ui.theme.TextSecondary
import com.avia.ui.theme.AlgoType
import com.avia.ui.components.AlgoGlyphs

/**
 * Step model for the Guided Walkthrough Tour.
 */
data class TourStep(
    val title: String,
    val subtitle: String,
    val description: String,
    val icon: ImageVector,
    val badgeText: String,
    val accentColor: Color
)

val TOUR_STEPS = listOf(
    TourStep(
        title = "Visualizer Canvas & Pointers",
        subtitle = "Real-time State & Element Indicators",
        description = "Observe dynamic array boxes, bars, or 2D graph nodes update step-by-step. Top pointer badges display pivots/targets, and bottom badges track scanning indices (i, j, mid).",
        icon = AlgoGlyphs.Bars,
        badgeText = "CANVAS & POINTERS",
        accentColor = PrimaryCyan
    ),
    TourStep(
        title = "Multi-Language Code Trace",
        subtitle = "Synchronized Code Inspection",
        description = "Toggle between Kotlin, Java, Python, and C++ using the segmented tabs. Active code lines highlight in lockstep with memory mutations and comparisons.",
        icon = AlgoGlyphs.Code,
        badgeText = "MULTI-LANGUAGE TRACE",
        accentColor = SecondaryPurple
    ),
    TourStep(
        title = "Live Variable State Inspector",
        subtitle = "Low-Level Variable Tracking",
        description = "Monitor live registers, loop counters (i, j), search bounds (low, mid, high), and memory call stack frames updated in real-time on every step.",
        icon = AlgoGlyphs.Tune,
        badgeText = "VARIABLE STATE",
        accentColor = AccentYellow
    ),
    TourStep(
        title = "Playback Controls & Scrubber",
        subtitle = "Step-by-Step & Speed Control",
        description = "Drag the timeline scrubber to jump anywhere in history. Use Step Back/Forward, Auto-Play, and toggle playback speed (0.5x, 1.0x, 2.0x).",
        icon = AlgoGlyphs.PlayCircle,
        badgeText = "TIMELINE SCRUBBER",
        accentColor = AccentGreen
    ),
    TourStep(
        title = "Predict Next Step Challenge",
        subtitle = "Interactive Knowledge Testing",
        description = "Enable Challenge Mode to test your algorithm intuition! Guess which elements will be compared or swapped next to earn streak points.",
        icon = AlgoGlyphs.Target,
        badgeText = "CHALLENGE MODE",
        accentColor = AccentPink
    ),
    TourStep(
        title = "Theory Deep Dive & AI Tutor",
        subtitle = "Complexity & Common Pitfalls",
        description = "Access the comprehensive Algorithm Theory sheet for Big-O proofs and common edge cases, or consult the offline AI Tutor for step-by-step explanations.",
        icon = AlgoGlyphs.Book,
        badgeText = "THEORY & AI TUTOR",
        accentColor = CyanGlow
    )
)

/**
 * Interactive Guided Walkthrough Coach Marks Overlay.
 */
@Composable
fun GuidedTourOverlay(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentStepIndex by remember { mutableIntStateOf(0) }
    val step = TOUR_STEPS[currentStepIndex]
    val totalSteps = TOUR_STEPS.size

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground.copy(alpha = 0.88f))
            .clickable { /* Block background touch */ },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(AlgoTokens.radiusLg))
                .background(CardBackground)
                .border(AlgoTokens.strokeThin, step.accentColor.copy(alpha = 0.5f), RoundedCornerShape(AlgoTokens.radiusLg))
                .padding(AlgoTokens.space6),
            verticalArrangement = Arrangement.spacedBy(AlgoTokens.space5)
        ) {
            // Header: Icon + Step Counter + Close Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space4)
                ) {
                    Box(
                        modifier = Modifier
                            .size(AlgoTokens.iconButtonLg)
                            .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                            .background(step.accentColor.copy(alpha = 0.15f))
                            .border(AlgoTokens.strokeThin, step.accentColor.copy(alpha = 0.4f), RoundedCornerShape(AlgoTokens.radiusSm)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = step.icon,
                            contentDescription = null,
                            tint = step.accentColor,
                            modifier = Modifier.size(AlgoTokens.inlineIconLg)
                        )
                    }

                    Column {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(AlgoTokens.radiusXs))
                                .background(step.accentColor.copy(alpha = 0.15f))
                                .padding(horizontal = AlgoTokens.space3, vertical = AlgoTokens.space1)
                        ) {
                            Text(
                                text = step.badgeText,
                                style = MaterialTheme.typography.labelSmall,
                                color = step.accentColor,
                                fontSize = AlgoType.microSize,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "Step ${currentStepIndex + 1} of $totalSteps",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted,
                            fontSize = AlgoType.microSize
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(AlgoTokens.iconButtonSm)
                        .clip(CircleShape)
                        .background(CanvasBackground)
                        .border(AlgoTokens.strokeThin, BorderSubtle, CircleShape)
                        .clickable { onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = AlgoGlyphs.Close,
                        contentDescription = "Close",
                        tint = TextMuted,
                        modifier = Modifier.size(AlgoTokens.inlineIconMd)
                    )
                }
            }

            // Title & Subtitle
            Column(verticalArrangement = Arrangement.spacedBy(AlgoTokens.space1)) {
                Text(
                    text = step.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = AlgoType.titleSize
                )
                Text(
                    text = step.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = step.accentColor,
                    fontSize = AlgoType.labelSize,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Description Body
            Text(
                text = step.description,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                fontSize = AlgoType.bodySize,
                lineHeight = AlgoType.leadingBody
            )

            // Progress Dots Indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TOUR_STEPS.forEachIndexed { idx, item ->
                    val isCurrent = idx == currentStepIndex
                    Box(
                        modifier = Modifier
                            .padding(horizontal = AlgoTokens.space1)
                            .size(
                                width = if (isCurrent) AlgoTokens.inlineIconLg else AlgoTokens.space3,
                                height = AlgoTokens.space3
                            )
                            .clip(RoundedCornerShape(AlgoTokens.radiusXl))
                            .background(if (isCurrent) item.accentColor else Color.White.copy(alpha = 0.15f))
                    )
                }
            }

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Skip or Back
                if (currentStepIndex > 0) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                            .background(CanvasBackground)
                            .border(AlgoTokens.strokeThin, BorderSubtle, RoundedCornerShape(AlgoTokens.radiusSm))
                            .clickable { currentStepIndex-- }
                            .padding(horizontal = AlgoTokens.space5, vertical = AlgoTokens.space4),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Back",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = AlgoType.labelSize
                        )
                    }
                } else {
                    Text(
                        text = "Skip Tour",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontSize = AlgoType.labelSize,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { onDismiss() }
                    )
                }

                // Next or Finish Button
                val isLast = currentStepIndex == totalSteps - 1
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                        .background(step.accentColor)
                        .clickable {
                            if (isLast) {
                                onDismiss()
                            } else {
                                currentStepIndex++
                            }
                        }
                        .padding(horizontal = AlgoTokens.space6, vertical = AlgoTokens.space4),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(AlgoTokens.space2)
                    ) {
                        Text(
                            text = if (isLast) "Got It, Let's Go!" else "Next",
                            style = MaterialTheme.typography.labelSmall,
                            color = DarkBackground,
                            fontWeight = FontWeight.Bold,
                            fontSize = AlgoType.labelSize
                        )
                        if (isLast) {
                            Icon(
                                imageVector = AlgoGlyphs.Check,
                                contentDescription = null,
                                tint = DarkBackground,
                                modifier = Modifier.size(AlgoTokens.inlineIconSm)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B0F19)
@Composable
fun GuidedTourOverlayPreview() {
    AlgoLensTheme {
        GuidedTourOverlay(onDismiss = {})
    }
}
