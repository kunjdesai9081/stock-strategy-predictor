package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.StockViewModel
import com.example.ui.screens.AiDeepDiveScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.PredictionDetailScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.WatchlistScreen
import com.example.ui.theme.BentoBorder
import com.example.ui.theme.BentoSurface
import com.example.ui.theme.BentoSurfaceVariant
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

enum class AppScreen(val title: String, val icon: ImageVector) {
    DASHBOARD("Dashboard", Icons.Default.ShowChart),
    PREDICTION("Target Studio", Icons.Default.AutoAwesome),
    AI_DEEP_DIVE("AI Rationale", Icons.Default.Psychology),
    WATCHLIST("Watchlist", Icons.Default.Bookmark),
    SETTINGS("Settings", Icons.Default.Settings)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                StockAiTargetApp()
            }
        }
    }
}

@Composable
fun StockAiTargetApp(stockViewModel: StockViewModel = viewModel()) {
    val isUnlocked by stockViewModel.isUnlocked.collectAsState()
    var currentScreen by remember { mutableStateOf(AppScreen.DASHBOARD) }

    if (!isUnlocked) {
        AuthScreen(
            onUnlockSuccess = { stockViewModel.unlockApp() }
        )
    } else {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                NavigationBar(
                    containerColor = BentoSurface,
                    contentColor = TextPrimary,
                    tonalElevation = 8.dp,
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .testTag("bottom_navigation_bar")
                ) {
                    AppScreen.values().forEach { screen ->
                        val selected = currentScreen == screen
                        NavigationBarItem(
                            selected = selected,
                            onClick = { currentScreen = screen },
                            icon = {
                                Icon(
                                    imageVector = screen.icon,
                                    contentDescription = screen.title,
                                    tint = if (selected) PrimaryBlue else TextMuted
                                )
                            },
                            label = {
                                Text(
                                    text = screen.title,
                                    fontSize = 10.sp,
                                    color = if (selected) PrimaryBlue else TextMuted
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = PrimaryBlue.copy(alpha = 0.2f)
                            ),
                            modifier = Modifier.testTag("nav_item_${screen.name.lowercase()}")
                        )
                    }
                }
            }
        ) { innerPadding ->
            val modifier = Modifier.padding(innerPadding)
            when (currentScreen) {
                AppScreen.DASHBOARD -> DashboardScreen(
                    viewModel = stockViewModel,
                    onNavigateToPrediction = { currentScreen = AppScreen.PREDICTION },
                    onNavigateToAiDeepDive = { currentScreen = AppScreen.AI_DEEP_DIVE },
                    modifier = modifier
                )
                AppScreen.PREDICTION -> PredictionDetailScreen(
                    viewModel = stockViewModel,
                    modifier = modifier
                )
                AppScreen.AI_DEEP_DIVE -> AiDeepDiveScreen(
                    viewModel = stockViewModel,
                    modifier = modifier
                )
                AppScreen.WATCHLIST -> WatchlistScreen(
                    viewModel = stockViewModel,
                    onNavigateToStock = { currentScreen = AppScreen.DASHBOARD },
                    modifier = modifier
                )
                AppScreen.SETTINGS -> SettingsScreen(
                    viewModel = stockViewModel,
                    modifier = modifier
                )
            }
        }
    }
}
