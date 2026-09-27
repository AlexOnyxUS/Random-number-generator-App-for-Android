package com.example.ui.screens

import android.graphics.Paint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.DonutLarge
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.WheelSectorData
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.LocalActiveGradient
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.MainUiState
import com.example.viewmodel.RngViewModel

@Composable
fun WheelScreen(
    viewModel: RngViewModel,
    uiState: MainUiState,
    modifier: Modifier = Modifier
) {
    val activeGradient = LocalActiveGradient.current
    var showManageDialog by remember { mutableStateOf(false) }
    var newSectorInput by remember { mutableStateOf("") }

    val currentRotation by animateFloatAsState(
        targetValue = uiState.targetWheelRotation,
        animationSpec = tween(durationMillis = 3000, easing = FastOutSlowInEasing),
        label = "wheel_spin_animation"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("wheel_screen_content"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 90.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Shake hint
        if (uiState.shakeEnabled) {
            item {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(DarkSurfaceVariant)
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ScreenRotation,
                        contentDescription = null,
                        tint = activeGradient.primaryColor,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = "Встряхните телефон, чтобы крутить колесо",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Sector Control Bar (Allows user to control sections and section count)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "СЕКТОРЫ КОЛЕСА (${uiState.wheelSectors.size})",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Нажмите, чтобы настроить текст и количество",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Button(
                            onClick = { showManageDialog = true },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = null,
                                tint = activeGradient.primaryColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.size(6.dp))
                            Text(
                                text = "Настроить",
                                color = activeGradient.primaryColor,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Wheel Display Card
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
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = if (uiState.isWheelSpinning) "КОЛЕСО ВРАЩАЕТСЯ…" else "КОЛЕСО ФОРТУНЫ",
                        color = activeGradient.primaryColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )

                    // Wheel container with top needle
                    Box(
                        modifier = Modifier
                            .size(260.dp)
                            .padding(top = 8.dp),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        // Spinning Wheel Canvas
                        Canvas(
                            modifier = Modifier
                                .size(240.dp)
                                .align(Alignment.Center)
                                .clickable(enabled = !uiState.isWheelSpinning) {
                                    viewModel.spinWheel()
                                }
                        ) {
                            val center = Offset(size.width / 2f, size.height / 2f)
                            val radius = size.width / 2f
                            val sectors = uiState.wheelSectors
                            val sectorAngle = 360f / sectors.size

                            rotate(currentRotation, pivot = center) {
                                sectors.forEachIndexed { index, sector ->
                                    val startAngle = index * sectorAngle
                                    drawArc(
                                        color = Color(sector.colorHex),
                                        startAngle = startAngle,
                                        sweepAngle = sectorAngle,
                                        useCenter = true,
                                        topLeft = Offset.Zero,
                                        size = size,
                                        style = Fill
                                    )

                                    drawArc(
                                        color = Color(0xFF0F172A).copy(alpha = 0.6f),
                                        startAngle = startAngle,
                                        sweepAngle = sectorAngle,
                                        useCenter = true,
                                        topLeft = Offset.Zero,
                                        size = size,
                                        style = Stroke(width = 1.5.dp.toPx())
                                    )

                                    val textAngle = startAngle + sectorAngle / 2f
                                    rotate(textAngle, pivot = center) {
                                        val paint = Paint().apply {
                                            color = android.graphics.Color.WHITE
                                            textSize = (if (sectors.size > 12) 10.sp else 12.sp).toPx()
                                            isAntiAlias = true
                                            isFakeBoldText = true
                                            textAlign = Paint.Align.RIGHT
                                            setShadowLayer(4f, 1f, 1f, android.graphics.Color.BLACK)
                                        }

                                        val displayText = if (sector.text.length > 10) {
                                            sector.text.take(9) + "…"
                                        } else {
                                            sector.text
                                        }

                                        drawContext.canvas.nativeCanvas.drawText(
                                            displayText,
                                            center.x + radius - 20.dp.toPx(),
                                            center.y + 4.dp.toPx(),
                                            paint
                                        )
                                    }
                                }

                                drawCircle(
                                    color = Color.White.copy(alpha = 0.6f),
                                    radius = radius,
                                    center = center,
                                    style = Stroke(width = 3.dp.toPx())
                                )
                            }

                            // Center Hub
                            drawCircle(color = Color(0xFF0F172A), radius = 24.dp.toPx(), center = center)
                            drawCircle(color = activeGradient.primaryColor, radius = 18.dp.toPx(), center = center)
                            drawCircle(color = Color.White, radius = 8.dp.toPx(), center = center)
                        }

                        // Top Pointer Needle
                        Canvas(
                            modifier = Modifier
                                .size(24.dp)
                                .align(Alignment.TopCenter)
                        ) {
                            val path = Path().apply {
                                moveTo(size.width / 2f, size.height)
                                lineTo(0f, 0f)
                                lineTo(size.width, 0f)
                                close()
                            }
                            drawPath(path = path, color = Color.White)
                            drawPath(
                                path = path,
                                color = activeGradient.primaryColor,
                                style = Stroke(width = 2.dp.toPx())
                            )
                        }
                    }

                    // Result Banner
                    AnimatedVisibility(
                        visible = uiState.winningSector != null && !uiState.isWheelSpinning,
                        enter = fadeIn() + scaleIn()
                    ) {
                        val winner = uiState.winningSector
                        if (winner != null) {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(DarkSurfaceVariant)
                                    .border(1.dp, activeGradient.primaryColor, RoundedCornerShape(12.dp))
                                    .padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = activeGradient.primaryColor,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Выпало: ${winner.text}",
                                    color = TextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Spin Action Button
        item {
            Button(
                onClick = { viewModel.spinWheel() },
                enabled = !uiState.isWheelSpinning,
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp)
                    .height(52.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(activeGradient.brush)
                    .testTag("spin_wheel_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = if (uiState.isWheelSpinning) Icons.Default.Refresh else Icons.Outlined.DonutLarge,
                        contentDescription = null,
                        tint = Color(0xFF0F172A),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = if (uiState.isWheelSpinning) "КОЛЕСО ВРАЩАЕТСЯ…" else "КРУТИТЬ КОЛЕСО",
                        color = Color(0xFF0F172A),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.2.sp
                    )
                }
            }
        }
    }

    // Sector Management Dialog
    if (showManageDialog) {
        AlertDialog(
            onDismissRequest = { showManageDialog = false },
            containerColor = DarkSurface,
            title = {
                Text(
                    text = "Настройка секторов колеса",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(380.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Quick presets
                    Text(text = "Готовые наборы:", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        QuickWheelChip("Да/Нет (2)", onClick = { viewModel.setWheelPreset("YES_NO") })
                        QuickWheelChip("Кубик (6)", onClick = { viewModel.setWheelPreset("DICE_6") })
                        QuickWheelChip("Призы (6)", onClick = { viewModel.setWheelPreset("PRIZES") })
                    }

                    // Add new sector input
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = newSectorInput,
                            onValueChange = { newSectorInput = it },
                            placeholder = { Text("Новый сектор…", fontSize = 12.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = activeGradient.primaryColor,
                                unfocusedBorderColor = DarkCardBorder,
                                focusedContainerColor = DarkSurfaceVariant,
                                unfocusedContainerColor = DarkSurfaceVariant
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = {
                                if (newSectorInput.isNotBlank()) {
                                    viewModel.addWheelSector(newSectorInput)
                                    newSectorInput = ""
                                }
                            },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(activeGradient.primaryColor)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Добавить", tint = Color(0xFF0F172A))
                        }
                    }

                    Text(
                        text = "Список секторов (${uiState.wheelSectors.size}):",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // Sector Items List
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(uiState.wheelSectors, key = { it.id }) { sector ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(DarkSurfaceVariant)
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(14.dp)
                                            .clip(CircleShape)
                                            .background(Color(sector.colorHex))
                                    )
                                    Text(
                                        text = sector.text,
                                        color = TextPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                if (uiState.wheelSectors.size > 2) {
                                    IconButton(
                                        onClick = { viewModel.removeWheelSector(sector.id) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Удалить",
                                            tint = TextMuted,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showManageDialog = false }) {
                    Text("Готово", color = activeGradient.primaryColor, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.resetWheelSectorsToDefault() }) {
                    Text("Сброс", color = TextMuted)
                }
            }
        )
    }
}

@Composable
private fun QuickWheelChip(title: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(DarkSurfaceVariant)
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 5.dp)
    ) {
        Text(text = title, color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}
