package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SavedRangePreset
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.ErrorColor
import com.example.ui.theme.LocalActiveGradient
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.MainUiState
import com.example.viewmodel.RngViewModel
import kotlinx.coroutines.delay

@Composable
fun NumberScreen(
    viewModel: RngViewModel,
    uiState: MainUiState,
    modifier: Modifier = Modifier
) {
    val activeGradient = LocalActiveGradient.current
    val focusManager = LocalFocusManager.current
    var copiedState by remember { mutableStateOf(false) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var presetNameInput by remember { mutableStateOf("") }

    LaunchedEffect(copiedState) {
        if (copiedState) {
            delay(1500)
            copiedState = false
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("number_screen_content"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 90.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Shake hint badge (compact)
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
                        text = "Встряхните телефон для генерации",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Clean Result Display Card
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
                        .padding(horizontal = 18.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ДИАПАЗОН: [${uiState.minInput} … ${uiState.maxInput}]",
                            color = activeGradient.primaryColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        if (copiedState) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(15.dp)
                                )
                                Text(
                                    text = "Скопировано",
                                    color = Color(0xFF10B981),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        } else {
                            IconButton(
                                onClick = {
                                    val joined = uiState.numberResults.joinToString(", ")
                                    viewModel.copyToClipboard(joined)
                                    copiedState = true
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Копировать",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    if (uiState.displayNumbers.size <= 1) {
                        val num = uiState.displayNumbers.firstOrNull() ?: "0"
                        val scrollState = rememberScrollState()
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(72.dp)
                                .horizontalScroll(scrollState),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = num,
                                color = if (uiState.isNumberRolling) activeGradient.primaryColor else TextPrimary,
                                fontSize = if (num.length > 8) 32.sp else 46.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            uiState.displayNumbers.forEachIndexed { idx, valStr ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(DarkSurfaceVariant)
                                        .clickable {
                                            viewModel.copyToClipboard(valStr)
                                            copiedState = true
                                        }
                                        .padding(horizontal = 12.dp, vertical = 7.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "#${idx + 1}",
                                        color = activeGradient.secondaryColor,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = valStr,
                                        color = TextPrimary,
                                        fontSize = 15.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Range Limits Card (Min and Max apply immediately)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ГРАНИЦЫ ДИАПАЗОНА",
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(DarkSurfaceVariant)
                                .clickable {
                                    presetNameInput = "Диапазон ${uiState.minInput} - ${uiState.maxInput}"
                                    showSaveDialog = true
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bookmark,
                                contentDescription = null,
                                tint = activeGradient.primaryColor,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Сохранить пресет",
                                color = activeGradient.primaryColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = uiState.minInput,
                            onValueChange = { viewModel.updateMinInput(it) },
                            label = { Text("От (Min)", fontSize = 12.sp) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next
                            ),
                            colors = cleanFieldColors(activeGradient.primaryColor),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = uiState.maxInput,
                            onValueChange = { viewModel.updateMaxInput(it) },
                            label = { Text("До (Max)", fontSize = 12.sp) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            colors = cleanFieldColors(activeGradient.primaryColor),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Batch and Unique Controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Количество чисел: ${uiState.batchCount}",
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Выдача нескольких чисел сразу",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(DarkSurfaceVariant)
                                    .clickable(enabled = uiState.batchCount > 1) {
                                        viewModel.updateBatchCount(uiState.batchCount - 1)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("−", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                            Text(
                                text = uiState.batchCount.toString(),
                                color = activeGradient.primaryColor,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(DarkSurfaceVariant)
                                    .clickable(enabled = uiState.batchCount < 50) {
                                        viewModel.updateBatchCount(uiState.batchCount + 1)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("+", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Только уникальные",
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Switch(
                            checked = uiState.uniqueOnly,
                            onCheckedChange = { viewModel.toggleUniqueOnly(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = activeGradient.primaryColor,
                                checkedTrackColor = activeGradient.primaryColor.copy(alpha = 0.35f)
                            )
                        )
                    }
                }
            }
        }

        // Saved Range Presets Section
        if (uiState.savedPresets.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 600.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "СОХРАНЁННЫЕ ПРЕСЕТЫ",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(uiState.savedPresets, key = { it.id }) { preset ->
                                SavedPresetPill(
                                    preset = preset,
                                    isSelected = uiState.minInput == preset.min && uiState.maxInput == preset.max,
                                    accentColor = activeGradient.primaryColor,
                                    onClick = { viewModel.loadPreset(preset) },
                                    onDelete = { viewModel.deletePreset(preset.id) }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Validation Error message
        if (uiState.numberErrorMessage != null) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(ErrorColor.copy(alpha = 0.15f))
                        .border(1.dp, ErrorColor.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.ErrorOutline, contentDescription = null, tint = ErrorColor, modifier = Modifier.size(18.dp))
                    Text(text = uiState.numberErrorMessage, color = Color(0xFFFFB4BA), fontSize = 12.sp)
                }
            }
        }

        // Generate Action Button
        item {
            Button(
                onClick = {
                    focusManager.clearFocus()
                    viewModel.generateNumbers(animate = true)
                },
                enabled = !uiState.isNumberRolling,
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp)
                    .height(52.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(activeGradient.brush)
                    .testTag("generate_numbers_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = if (uiState.isNumberRolling) Icons.Default.Refresh else Icons.Default.Casino,
                        contentDescription = null,
                        tint = Color(0xFF0F172A),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = if (uiState.isNumberRolling) "ГЕНЕРАЦИЯ…" else "СГЕНЕРИРОВАТЬ",
                        color = Color(0xFF0F172A),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.2.sp
                    )
                }
            }
        }
    }

    // Dialog to save current range as preset
    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            containerColor = DarkSurface,
            title = {
                Text(
                    text = "Сохранить пресет",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Диапазон: от ${uiState.minInput} до ${uiState.maxInput}",
                        color = TextMuted,
                        fontSize = 13.sp
                    )
                    OutlinedTextField(
                        value = presetNameInput,
                        onValueChange = { presetNameInput = it },
                        label = { Text("Название пресета") },
                        singleLine = true,
                        colors = cleanFieldColors(activeGradient.primaryColor),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.saveCurrentRangeAsPreset(presetNameInput)
                        showSaveDialog = false
                    }
                ) {
                    Text("Сохранить", color = activeGradient.primaryColor, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) {
                    Text("Отмена", color = TextMuted)
                }
            }
        )
    }
}

@Composable
private fun SavedPresetPill(
    preset: SavedRangePreset,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) DarkSurfaceVariant else Color.Transparent)
            .border(
                1.dp,
                if (isSelected) accentColor else DarkCardBorder,
                RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(start = 12.dp, top = 6.dp, bottom = 6.dp, end = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Column {
            Text(
                text = preset.name,
                color = if (isSelected) accentColor else TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "[${preset.min} … ${preset.max}]",
                color = TextMuted,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )
        }
        IconButton(
            onClick = onDelete,
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Удалить пресет",
                tint = TextMuted,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
private fun cleanFieldColors(accentColor: Color) = OutlinedTextFieldDefaults.colors(
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary,
    focusedBorderColor = accentColor,
    unfocusedBorderColor = DarkCardBorder,
    cursorColor = accentColor,
    focusedContainerColor = DarkSurfaceVariant,
    unfocusedContainerColor = DarkSurfaceVariant
)
