package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.outlined.DonutLarge
import androidx.compose.material.icons.outlined.MonetizationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.LocalActiveGradient
import com.example.ui.theme.TextMuted
import com.example.viewmodel.AppTab

@Composable
fun BottomFloatingBar(
    currentTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val activeGradient = LocalActiveGradient.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .widthIn(max = 380.dp)
                .fillMaxWidth()
                .shadow(12.dp, RoundedCornerShape(26.dp), ambientColor = activeGradient.primaryColor.copy(alpha = 0.2f))
                .border(1.dp, DarkCardBorder, RoundedCornerShape(26.dp)),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FloatingTabItem(
                    title = "Числа",
                    icon = Icons.Default.Casino,
                    isSelected = currentTabIndex == 0,
                    onClick = { onTabSelected(0) },
                    modifier = Modifier.weight(1f)
                )

                FloatingTabItem(
                    title = "Монетка",
                    icon = Icons.Outlined.MonetizationOn,
                    isSelected = currentTabIndex == 1,
                    onClick = { onTabSelected(1) },
                    modifier = Modifier.weight(1f)
                )

                FloatingTabItem(
                    title = "Колесо",
                    icon = Icons.Outlined.DonutLarge,
                    isSelected = currentTabIndex == 2,
                    onClick = { onTabSelected(2) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun FloatingTabItem(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeGradient = LocalActiveGradient.current
    val interactionSource = remember { MutableInteractionSource() }

    val bgModifier = if (isSelected) {
        Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(activeGradient.brush)
    } else {
        Modifier.clip(RoundedCornerShape(20.dp))
    }

    Box(
        modifier = modifier
            .then(bgModifier)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .padding(vertical = 6.dp, horizontal = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) Color(0xFF0F172A) else TextMuted,
                modifier = Modifier.size(17.dp)
            )
            Text(
                text = title,
                color = if (isSelected) Color(0xFF0F172A) else TextMuted,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium
            )
        }
    }
}
