package com.example.algolens.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import com.example.algolens.ui.theme.AlgoTokens
import com.example.algolens.ui.theme.AlgoType
import com.example.algolens.ui.theme.BorderSubtle
import com.example.algolens.ui.theme.CardBackgroundElevated
import com.example.algolens.ui.theme.CyanSubtle
import com.example.algolens.ui.theme.PrimaryCyan
import com.example.algolens.ui.theme.TextDark

enum class NavTab(val label: String, val icon: ImageVector) {
    HOME("Home", Icons.Default.Home),
    EXPLORE("Explore", Icons.Default.Explore),
    PROFILE("Profile", Icons.Default.Person),
    SETTINGS("Settings", Icons.Default.Tune)
}

@Composable
fun BottomNavBar(
    activeTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(CardBackgroundElevated)
            .border(width = AlgoTokens.strokeThin, color = BorderSubtle)
            .navigationBarsPadding()
            .padding(horizontal = AlgoTokens.space2, vertical = AlgoTokens.space3),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        NavTab.entries.forEach { tab ->
            val isActive = activeTab == tab
            val interactionSource = remember { MutableInteractionSource() }

            val iconColor by animateColorAsState(
                targetValue = if (isActive) PrimaryCyan else TextDark,
                label = "iconColor"
            )
            val pillBg by animateColorAsState(
                targetValue = if (isActive) CyanSubtle else Color.Transparent,
                label = "pillBg"
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) { onTabSelected(tab) }
                    .padding(vertical = AlgoTokens.space1),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(AlgoTokens.space1)
            ) {
                Box(
                    modifier = Modifier
                        .size(
                            width = AlgoTokens.iconButtonLg + AlgoTokens.space1,
                            height = AlgoTokens.iconButtonXs + AlgoTokens.space1
                        )
                        .clip(RoundedCornerShape(AlgoTokens.radiusSm))
                        .background(pillBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.label,
                        tint = iconColor,
                        modifier = Modifier.size(AlgoTokens.inlineIconLg + AlgoTokens.space1)
                    )
                }

                Text(
                    text = tab.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isActive) PrimaryCyan else TextDark,
                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                    fontSize = AlgoType.labelSize
                )
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, backgroundColor = 0xFF0B0F19)
@Composable
fun BottomNavBarPreview() {
    com.example.algolens.ui.theme.AlgoLensTheme {
        BottomNavBar(
            activeTab = NavTab.HOME,
            onTabSelected = {}
        )
    }
}