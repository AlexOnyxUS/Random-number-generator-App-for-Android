package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.MonetizationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.LocalActiveGradient
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.CoinSide
import com.example.viewmodel.MainUiState
import com.example.viewmodel.RngViewModel
import kotlinx.coroutines.launch

@Composable
fun CoinScreen(
    viewModel: RngViewModel,
    uiState: MainUiState,
    modifier: Modifier = Modifier
) {
    val activeGradient = LocalActiveGradient.current
    val density = LocalDensity.current

    val rotXAnim = remember { Animatable(0f) }
    val rotYAnim = remember { Animatable(0f) }
    val rotZAnim = remember { Animatable(0f) }
    val scaleAnim = remember { Animatable(1f) }

    LaunchedEffect(uiState.isCoinFlipping) {
        if (uiState.isCoinFlipping) {
            val isHeads = uiState.coinResult == CoinSide.HEADS

            // Altitude parabolic bounce
            launch {
                scaleAnim.animateTo(
                    targetValue = 1.18f,
                    animationSpec = tween(durationMillis = 360, easing = CubicBezierEasing(0.2f, 0.8f, 0.4f, 1f))
                )
                scaleAnim.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 440, easing = CubicBezierEasing(0.6f, 0f, 0.8f, 0.2f))
                )
            }

            // Subtle 3D tilt in the air (wobble on X and Z) that ALWAYS returns to 0 on landing!
            launch {
                rotXAnim.animateTo(22f, tween(350, easing = FastOutSlowInEasing))
                rotXAnim.animateTo(0f, tween(450, easing = FastOutSlowInEasing))
            }
            launch {
                rotZAnim.animateTo(12f, tween(300, easing = FastOutSlowInEasing))
                rotZAnim.animateTo(0f, tween(500, easing = FastOutSlowInEasing))
            }

            // Flip around Y axis:
            // If Heads: ends at a multiple of 360° (0 mod 360)
            // If Tails: ends at a multiple of 360° + 180° (180 mod 360)
            launch {
                val currentY = rotYAnim.value
                val fullSpins = 5 * 360f // 1800 deg
                val currentMod = (currentY % 360f + 360f) % 360f
                val targetMod = if (isHeads) 0f else 180f
                val diff = (targetMod - currentMod + 360f) % 360f
                val targetY = currentY + fullSpins + diff

                rotYAnim.animateTo(
                    targetValue = targetY,
                    animationSpec = tween(
                        durationMillis = 800,
                        easing = CubicBezierEasing(0.2f, 0.05f, 0.2f, 1f)
                    )
                )
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("coin_screen_content"),
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
                        text = "Встряхните телефон, чтобы бросить монетку",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Coin Display Card
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
                        .padding(horizontal = 20.dp, vertical = 22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    Text(
                        text = if (uiState.isCoinFlipping) "МОНЕТКА В ПОЛЁТЕ…" else "ВЫПАЛО: ${uiState.coinResult.title.uppercase()}",
                        color = activeGradient.primaryColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )

                    // 3D Animated Coin (Guaranteed right side up!)
                    Box(
                        modifier = Modifier
                            .size(176.dp)
                            .graphicsLayer {
                                this.rotationX = rotXAnim.value
                                this.rotationY = rotYAnim.value
                                this.rotationZ = rotZAnim.value
                                this.scaleX = scaleAnim.value
                                this.scaleY = scaleAnim.value
                                this.cameraDistance = 16f * density.density
                            }
                            .clickable(enabled = !uiState.isCoinFlipping) {
                                viewModel.flipCoin()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        // Check which face is towards the viewer
                        val normY = (rotYAnim.value % 360f + 360f) % 360f
                        val isShowingHeads = normY in 0f..90f || normY in 270f..360f

                        if (isShowingHeads) {
                            CoinHeadsSide(activeGradientColor = activeGradient.primaryColor)
                        } else {
                            // Rotate Tails 180° around Y so it faces the camera un-mirrored and strictly upright!
                            Box(modifier = Modifier.graphicsLayer { rotationY = 180f }) {
                                CoinTailsSide(secondaryColor = activeGradient.secondaryColor)
                            }
                        }
                    }

                    Text(
                        text = "Нажмите на монетку для броска",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Statistics Card
        item {
            val total = uiState.headsCount + uiState.tailsCount
            val headsPct = if (total > 0) (uiState.headsCount * 100f / total).toInt() else 50
            val tailsPct = if (total > 0) 100 - headsPct else 50

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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "СТАТИСТИКА БРОСКОВ (Всего: $total)",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (total > 0) {
                            IconButton(
                                onClick = { viewModel.resetCoinStats() },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.RestartAlt,
                                    contentDescription = "Сброс",
                                    tint = TextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatPill(
                            title = "Орёл",
                            count = uiState.headsCount,
                            pct = headsPct,
                            color = activeGradient.primaryColor,
                            modifier = Modifier.weight(1f)
                        )
                        StatPill(
                            title = "Решка",
                            count = uiState.tailsCount,
                            pct = tailsPct,
                            color = activeGradient.secondaryColor,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Action Button
        item {
            Button(
                onClick = { viewModel.flipCoin() },
                enabled = !uiState.isCoinFlipping,
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp)
                    .height(52.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(activeGradient.brush)
                    .testTag("flip_coin_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.MonetizationOn,
                        contentDescription = null,
                        tint = Color(0xFF0F172A),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = if (uiState.isCoinFlipping) "БРОСОК…" else "ПОДБРОСИТЬ МОНЕТКУ",
                        color = Color(0xFF0F172A),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.2.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun CoinHeadsSide(activeGradientColor: Color) {
    Box(
        modifier = Modifier
            .size(176.dp)
            .shadow(10.dp, CircleShape)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(Color(0xFF2E3D5B), Color(0xFF161F2E), Color(0xFF0D131D))
                )
            )
            .border(3.5.dp, Brush.linearGradient(listOf(activeGradientColor, Color.White, activeGradientColor)), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = activeGradientColor,
                modifier = Modifier.size(48.dp)
            )
            Text(
                text = "ОРЁЛ",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )
        }
    }
}

@Composable
private fun CoinTailsSide(secondaryColor: Color) {
    Box(
        modifier = Modifier
            .size(176.dp)
            .shadow(10.dp, CircleShape)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(Color(0xFF3B2A4E), Color(0xFF221733), Color(0xFF110B1B))
                )
            )
            .border(3.5.dp, Brush.linearGradient(listOf(secondaryColor, Color.White, secondaryColor)), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = "1",
                color = secondaryColor,
                fontSize = 44.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = "РЕШКА",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )
        }
    }
}

@Composable
private fun StatPill(
    title: String,
    count: Int,
    pct: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(DarkSurfaceVariant)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Column {
            Text(text = title, color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(
                text = "$count раз ($pct%)",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
