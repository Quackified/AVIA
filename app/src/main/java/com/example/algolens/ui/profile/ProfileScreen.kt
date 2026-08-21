package com.example.algolens.ui.profile

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.algolens.data.SampleData
import com.example.algolens.model.Algorithm
import com.example.algolens.ui.components.AlgoCard
import com.example.algolens.ui.theme.AccentGreen
import com.example.algolens.ui.theme.AccentOrange
import com.example.algolens.ui.theme.BorderSubtle
import com.example.algolens.ui.theme.CanvasBackground
import com.example.algolens.ui.theme.CardBackground
import com.example.algolens.ui.theme.CyanGlow
import com.example.algolens.ui.theme.CyanSubtle
import com.example.algolens.ui.theme.DarkBackground
import com.example.algolens.ui.theme.OrangeSubtle
import com.example.algolens.ui.theme.PrimaryCyan
import com.example.algolens.ui.theme.PurpleGlow
import com.example.algolens.ui.theme.SecondaryPurple
import com.example.algolens.ui.theme.TextDark
import com.example.algolens.ui.theme.TextMuted
import com.example.algolens.ui.theme.TextNavy
import com.example.algolens.ui.theme.TextPrimary
import com.example.algolens.ui.theme.TextSecondary

@Composable
fun ProfileScreen(
    onAlgorithmClick: (Algorithm) -> Unit,
    modifier: Modifier = Modifier
) {
    val weeklyActivity = remember { listOf(3, 5, 2, 7, 4, 6, 3) }
    val days = remember { listOf("M", "T", "W", "T", "F", "S", "S") }
    val bookmarked = remember { SampleData.algorithms.filter { it.id in listOf(2, 5, 6) } }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CanvasBackground)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ── 1. Profile Hero ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Avatar
            Box(
                modifier = Modifier.size(56.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(CyanSubtle, SecondaryPurple.copy(alpha = 0.2f))
                            )
                        )
                        .border(1.5.dp, PrimaryCyan.copy(alpha = 0.35f), RoundedCornerShape(18.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "DD",
                        style = MaterialTheme.typography.titleLarge,
                        color = PrimaryCyan,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-1).sp
                    )
                }

                // Active dot
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(AccentGreen)
                        .border(2.dp, CanvasBackground, CircleShape)
                )
            }

            // Info
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    text = "Duke Ducky",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "@quacky · CS Student · Year 3",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    fontSize = 9.5.sp
                )

                Row(
                    modifier = Modifier.padding(top = 3.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(CyanSubtle)
                            .border(1.dp, PrimaryCyan.copy(alpha = 0.3f), CircleShape)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Beginner",
                            style = MaterialTheme.typography.labelSmall,
                            color = PrimaryCyan,
                            fontWeight = FontWeight.Bold,
                            fontSize = 8.5.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(OrangeSubtle)
                            .border(1.dp, AccentOrange.copy(alpha = 0.3f), CircleShape)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "🔥 Streak Active",
                            style = MaterialTheme.typography.labelSmall,
                            color = AccentOrange,
                            fontWeight = FontWeight.Bold,
                            fontSize = 8.5.sp
                        )
                    }
                }
            }
        }

        // ── 2. Stats Grid ──
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatCard(
                value = "14",
                label = "Mastered",
                sub = "algorithms",
                color = PrimaryCyan,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                value = "12",
                label = "Day Streak",
                sub = "days in a row",
                color = AccentOrange,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                value = "48",
                label = "Sessions",
                sub = "total",
                color = PurpleGlow,
                modifier = Modifier.weight(1f)
            )
        }

        // ── 3. Weekly Activity Chart ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(CardBackground)
                .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.BarChart,
                            contentDescription = null,
                            tint = PrimaryCyan,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "WEEKLY ACTIVITY",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextDark,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                    Text(
                        text = "30 sessions this week",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextNavy,
                        fontSize = 8.5.sp
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    weeklyActivity.forEachIndexed { i, valNum ->
                        val isToday = i == 6
                        val targetHeight = (valNum / 7f).coerceIn(0.1f, 1f)
                        val animatedFrac by animateFloatAsState(
                            targetValue = targetHeight,
                            animationSpec = tween(durationMillis = 500, delayMillis = i * 60),
                            label = "activityBar_$i"
                        )

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            verticalArrangement = Arrangement.Bottom,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth(),
                                contentAlignment = Alignment.BottomCenter
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight(animatedFrac)
                                        .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                                        .background(if (isToday) PrimaryCyan else CyanSubtle)
                                )
                            }

                            Spacer(modifier = Modifier.height(3.dp))

                            Text(
                                text = days[i],
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isToday) PrimaryCyan else TextDark,
                                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 8.sp
                            )
                        }
                    }
                }
            }
        }

        // ── 4. Bookmarked Section ──
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Bookmark,
                        contentDescription = null,
                        tint = PrimaryCyan,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = "BOOKMARKED",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextDark,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
                Text(
                    text = "View all →",
                    style = MaterialTheme.typography.labelSmall,
                    color = PrimaryCyan,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 9.sp
                )
            }

            bookmarked.forEach { algo ->
                AlgoCard(
                    algo = algo,
                    onClick = { onAlgorithmClick(algo) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun StatCard(
    value: String,
    label: String,
    sub: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(CardBackground)
            .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
            .padding(vertical = 10.dp, horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.displaySmall,
                color = color,
                fontWeight = FontWeight.Bold,
                fontSize = 19.sp
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 8.5.sp
            )
            Text(
                text = sub,
                style = MaterialTheme.typography.bodySmall,
                color = TextDark,
                fontSize = 7.5.sp
            )
        }
    }
}
