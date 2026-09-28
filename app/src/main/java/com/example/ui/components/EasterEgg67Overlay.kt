package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalActiveGradient
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

/**
 * Animated edge border effect triggered when number 67 is generated.
 * Features glowing pulsating screen borders and text smoothly oscillating back and forth (ping-pong marquee).
 */
@Composable
fun EasterEgg67Overlay(
    visible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!visible) return

    val activeGradient = LocalActiveGradient.current

    // Auto-dismiss after 7 seconds
    LaunchedEffect(visible) {
        if (visible) {
            delay(7000L)
            onDismiss()
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "easter_egg_67_transition")

    // Ping-pong horizontal oscillation (-60dp to +60dp)
    val horizontalOffset by infiniteTransition.animateFloat(
        initialValue = -70f,
        targetValue = 70f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ping_pong_h"
    )

    // Ping-pong vertical oscillation for left & right borders
    val verticalOffset by infiniteTransition.animateFloat(
        initialValue = -50f,
        targetValue = 50f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ping_pong_v"
    )

    // Glow pulse animation
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(300)),
        exit = fadeOut(tween(300)),
        modifier = modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss
                )
        ) {
            // Neon edge border around entire viewport
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(
                        width = 4.dp,
                        brush = Brush.sweepGradient(
                            listOf(
                                activeGradient.primaryColor.copy(alpha = glowAlpha),
                                activeGradient.secondaryColor.copy(alpha = glowAlpha),
                                Color(0xFFFFD700).copy(alpha = glowAlpha),
                                activeGradient.primaryColor.copy(alpha = glowAlpha)
                            )
                        ),
                        shape = androidx.compose.ui.graphics.RectangleShape
                    )
            )

            // TOP OSCILLATING MARQUEE BORDER
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF0F172A).copy(alpha = 0.95f), Color.Transparent)
                        )
                    )
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = "✨ 67 ✨ LUCKY NUMBER 67 ✨ 67 ✨ EASTER EGG 67 ✨ 67 ✨ WINNER ✨ 67 ✨",
                    color = activeGradient.primaryColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    maxLines = 1,
                    modifier = Modifier
                        .offset { IntOffset(horizontalOffset.roundToInt(), 0) }
                        .align(Alignment.Center)
                )
            }

            // BOTTOM OSCILLATING MARQUEE BORDER
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color(0xFF0F172A).copy(alpha = 0.95f))
                        )
                    )
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = "★ 67 ★ JACKPOT 67 ★ 67 ★ PING-PONG 67 ★ 67 ★ LUCKY 67 ★ 67 ★",
                    color = activeGradient.secondaryColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    maxLines = 1,
                    modifier = Modifier
                        .offset { IntOffset((-horizontalOffset).roundToInt(), 0) }
                        .align(Alignment.Center)
                )
            }

            // LEFT OSCILLATING MARQUEE BORDER
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset { IntOffset(0, verticalOffset.roundToInt()) }
                    .padding(start = 6.dp)
            ) {
                Text(
                    text = "◆\n6\n7\n◆\n★\n◆\n6\n7\n◆",
                    color = activeGradient.primaryColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    lineHeight = 14.sp
                )
            }

            // RIGHT OSCILLATING MARQUEE BORDER
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .offset { IntOffset(0, (-verticalOffset).roundToInt()) }
                    .padding(end = 6.dp)
            ) {
                Text(
                    text = "★\n6\n7\n★\n◆\n★\n6\n7\n★",
                    color = activeGradient.secondaryColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    lineHeight = 14.sp
                )
            }

            // CENTER CELEBRATION BADGE
            Card(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(24.dp)
                    .testTag("easter_egg_67_badge"),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0D131F).copy(alpha = 0.96f)),
                border = androidx.compose.foundation.BorderStroke(2.dp, activeGradient.primaryColor)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 28.dp, vertical = 22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "EASTER EGG 67!",
                            color = Color(0xFFFFD700),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.5.sp
                        )
                    }

                    Text(
                        text = "67",
                        color = Color.White,
                        fontSize = 68.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )

                    Text(
                        text = "Счастливое число 67 выпало в генераторе!",
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.1f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Закрыть",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
