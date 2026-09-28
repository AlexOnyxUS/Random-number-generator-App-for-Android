package com.example.ui.navigation

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.BottomFloatingBar
import com.example.ui.components.EasterEgg67Overlay
import com.example.ui.screens.CoinScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.NumberScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.WheelScreen
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.LocalActiveGradient
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.VibrationHelper
import com.example.viewmodel.AppTab
import com.example.viewmodel.MainUiState
import com.example.viewmodel.RngViewModel
import com.example.viewmodel.UiEvent
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

enum class AppSubScreen {
    MAIN,
    SETTINGS,
    HISTORY
}

@Composable
fun AppNavigation(
    viewModel: RngViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var subScreen by remember { mutableStateOf(AppSubScreen.MAIN) }
    val context = LocalContext.current
    val vibrationHelper = remember { VibrationHelper(context) }
    val scope = rememberCoroutineScope()

    // Horizontal Pager for switching tabs by both sliding and tapping
    val pagerState = rememberPagerState(pageCount = { 3 })

    // Sync pager state with viewModel currentTab
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            val tab = when (page) {
                0 -> AppTab.NUMBERS
                1 -> AppTab.COIN
                else -> AppTab.WHEEL
            }
            if (uiState.currentTab != tab) {
                viewModel.setTab(tab)
            }
        }
    }

    if (subScreen != AppSubScreen.MAIN) {
        BackHandler { subScreen = AppSubScreen.MAIN }
    }

    // Rich Vibration Patterns and Toast Events
    LaunchedEffect(Unit) {
        viewModel.uiEvents.collectLatest { event ->
            when (event) {
                is UiEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                is UiEvent.TriggerTick -> {
                    if (uiState.hapticsEnabled) vibrationHelper.tick()
                }
                is UiEvent.TriggerCoinLanding -> {
                    if (uiState.hapticsEnabled) vibrationHelper.coinLandingPattern()
                }
                is UiEvent.TriggerWheelNotch -> {
                    if (uiState.hapticsEnabled) vibrationHelper.wheelNotchTick()
                }
                is UiEvent.TriggerVictory -> {
                    if (uiState.hapticsEnabled) vibrationHelper.victoryPattern()
                }
            }
        }
    }

    CompositionLocalProvider(LocalActiveGradient provides uiState.gradientTheme) {
        val activeGradient = LocalActiveGradient.current

        Scaffold(
            containerColor = DarkBackground,
            modifier = modifier.fillMaxSize()
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                AnimatedContent(
                    targetState = subScreen,
                    transitionSpec = {
                        if (targetState == AppSubScreen.MAIN) {
                            (slideInHorizontally { -it } + fadeIn())
                                .togetherWith(slideOutHorizontally { it } + fadeOut())
                        } else {
                            (slideInHorizontally { it } + fadeIn())
                                .togetherWith(slideOutHorizontally { -it } + fadeOut())
                        }
                    },
                    label = "subscreen_transition"
                ) { screen ->
                    when (screen) {
                        AppSubScreen.SETTINGS -> {
                            SettingsScreen(
                                viewModel = viewModel,
                                uiState = uiState,
                                onNavigateBack = { subScreen = AppSubScreen.MAIN }
                            )
                        }
                        AppSubScreen.HISTORY -> {
                            HistoryScreen(
                                viewModel = viewModel,
                                uiState = uiState,
                                onNavigateBack = { subScreen = AppSubScreen.MAIN }
                            )
                        }
                        AppSubScreen.MAIN -> {
                            Column(modifier = Modifier.fillMaxSize()) {
                                // Compact, accurate Top Header Bar
                                CompactTopHeader(
                                    onOpenHistory = { subScreen = AppSubScreen.HISTORY },
                                    onOpenSettings = { subScreen = AppSubScreen.SETTINGS }
                                )

                                // Main Content with Horizontal Pager (supports tapping & sliding!)
                                Box(modifier = Modifier.weight(1f)) {
                                    HorizontalPager(
                                        state = pagerState,
                                        modifier = Modifier.fillMaxSize()
                                    ) { page ->
                                        when (page) {
                                            0 -> NumberScreen(viewModel = viewModel, uiState = uiState)
                                            1 -> CoinScreen(viewModel = viewModel, uiState = uiState)
                                            2 -> WheelScreen(viewModel = viewModel, uiState = uiState)
                                        }
                                    }

                                    // Compact Floating Bottom Bar synced with pager
                                    BottomFloatingBar(
                                        currentTabIndex = pagerState.currentPage,
                                        onTabSelected = { pageIndex ->
                                            scope.launch {
                                                pagerState.animateScrollToPage(pageIndex)
                                            }
                                        },
                                        modifier = Modifier.align(Alignment.BottomCenter)
                                    )
                                }
                            }
                        }
                    }
                }

                // Animated edge border Easter Egg effect when 67 is generated
                EasterEgg67Overlay(
                    visible = uiState.showEasterEgg67,
                    onDismiss = { viewModel.dismissEasterEgg67() }
                )
            }
        }
    }
}

@Composable
private fun CompactTopHeader(
    onOpenHistory: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val activeGradient = LocalActiveGradient.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
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
                    .height(46.dp)
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(activeGradient.brush),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Casino,
                            contentDescription = null,
                            tint = Color(0xFF0F172A),
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Text(
                        text = "RNG STUDIO",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    IconButton(onClick = onOpenHistory, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "История",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(onClick = onOpenSettings, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Настройки",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
