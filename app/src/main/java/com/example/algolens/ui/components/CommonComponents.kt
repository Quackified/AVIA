package com.example.algolens.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WifiOff
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.algolens.ui.theme.AccentOrange
import com.example.algolens.ui.theme.CardBackground
import com.example.algolens.ui.theme.DarkBackground
import com.example.algolens.ui.theme.OrangeSubtle
import com.example.algolens.ui.theme.PrimaryCyan
import com.example.algolens.ui.theme.TextDark
import com.example.algolens.ui.theme.TextMuted
import com.example.algolens.ui.theme.TextPrimary
import com.example.algolens.ui.theme.TextSecondary

@Composable
fun SectionLabel(
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier,
    iconColor: Color = PrimaryCyan
) {
    Row(
        modifier = modifier.padding(bottom = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(12.dp)
        )
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = TextDark,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp
        )
    }
}

@Composable
fun CustomSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val trackColor by animateColorAsState(
        targetValue = if (checked) PrimaryCyan else Color(0xFF1A2540),
        label = "trackColor"
    )
    val thumbOffset by animateDpAsState(
        targetValue = if (checked) 20.dp else 2.dp,
        animationSpec = spring(stiffness = 500f, dampingRatio = 0.75f),
        label = "thumbOffset"
    )
    val thumbColor by animateColorAsState(
        targetValue = if (checked) DarkBackground else TextMuted,
        label = "thumbColor"
    )

    Box(
        modifier = modifier
            .width(42.dp)
            .height(24.dp)
            .clip(CircleShape)
            .background(trackColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onCheckedChange(!checked) }
            .padding(vertical = 2.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .offset(x = thumbOffset)
                .size(18.dp)
                .clip(CircleShape)
                .background(thumbColor)
        )
    }
}

@Composable
fun OfflineBadge(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(OrangeSubtle)
            .border(1.dp, AccentOrange.copy(alpha = 0.35f), CircleShape)
            .padding(horizontal = 7.dp, vertical = 2.5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = Icons.Default.WifiOff,
            contentDescription = "Offline",
            tint = AccentOrange,
            modifier = Modifier.size(9.dp)
        )
        Text(
            text = "Offline",
            style = MaterialTheme.typography.labelSmall,
            color = AccentOrange,
            fontWeight = FontWeight.Bold,
            fontSize = 8.5.sp
        )
    }
}

@Composable
fun ComplexityCard(
    timeComplexity: String,
    spaceComplexity: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(PrimaryCyan.copy(alpha = 0.05f))
            .border(1.dp, PrimaryCyan.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 9.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Time Complexity",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
                Text(
                    text = timeComplexity,
                    style = MaterialTheme.typography.labelMedium,
                    color = PrimaryCyan,
                    fontWeight = FontWeight.Bold
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Space Complexity",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
                Text(
                    text = spaceComplexity,
                    style = MaterialTheme.typography.labelMedium,
                    color = PrimaryCyan,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
