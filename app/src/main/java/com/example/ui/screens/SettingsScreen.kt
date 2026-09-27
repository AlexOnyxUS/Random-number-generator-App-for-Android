package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.GradientTheme
import com.example.ui.theme.LocalActiveGradient
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.MainUiState
import com.example.viewmodel.RngViewModel

@Composable
fun SettingsScreen(
    viewModel: RngViewModel,
    uiState: MainUiState,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeGradient = LocalActiveGradient.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("settings_screen_content"),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("settings_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад",
                            tint = TextPrimary
                        )
                    }
                    Text(
                        text = "НАСТРОЙКИ",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }

        // Gradient Themes Customization
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = null,
                            tint = activeGradient.primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "ЦВЕТОВОЙ ГРАДИЕНТ ТЕМЫ",
                            color = activeGradient.primaryColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }

                    GradientTheme.entries.forEach { theme ->
                        val isSelected = uiState.gradientTheme == theme
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) DarkSurfaceVariant else Color.Transparent)
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) theme.primaryColor else DarkCardBorder,
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable { viewModel.setGradientTheme(theme) }
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(theme.brush)
                                )
                                Text(
                                    text = theme.title,
                                    color = if (isSelected) TextPrimary else TextSecondary,
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }

                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(26.dp)
                                        .clip(CircleShape)
                                        .background(theme.primaryColor),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color(0xFF0F172A),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Sensor & Toggles Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "СЕНСОРЫ И УПРАВЛЕНИЕ",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // Shake to Generate
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ScreenRotation,
                                contentDescription = null,
                                tint = activeGradient.primaryColor,
                                modifier = Modifier.size(22.dp)
                            )
                            Column {
                                Text(
                                    text = "Встряхивание телефона",
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Генерация при встряхивании устройства",
                                    color = TextMuted,
                                    fontSize = 12.sp
                                )
                            }
                        }
                        Switch(
                            checked = uiState.shakeEnabled,
                            onCheckedChange = { viewModel.setShakeEnabled(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = activeGradient.primaryColor,
                                checkedTrackColor = activeGradient.primaryColor.copy(alpha = 0.35f)
                            ),
                            modifier = Modifier.testTag("shake_switch")
                        )
                    }

                    // Haptics toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Vibration,
                                contentDescription = null,
                                tint = activeGradient.primaryColor,
                                modifier = Modifier.size(22.dp)
                            )
                            Column {
                                Text(
                                    text = "Тактильный отклик (Haptic)",
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Вибрация при вращении и генерации",
                                    color = TextMuted,
                                    fontSize = 12.sp
                                )
                            }
                        }
                        Switch(
                            checked = uiState.hapticsEnabled,
                            onCheckedChange = { viewModel.setHapticsEnabled(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = activeGradient.primaryColor,
                                checkedTrackColor = activeGradient.primaryColor.copy(alpha = 0.35f)
                            ),
                            modifier = Modifier.testTag("haptic_switch")
                        )
                    }
                }
            }
        }
    }
}
