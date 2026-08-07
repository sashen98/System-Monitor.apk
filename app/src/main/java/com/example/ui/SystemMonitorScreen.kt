package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.ActiveAppsList
import com.example.ui.components.BatteryCenterGauge
import com.example.ui.components.CalendarWidget
import com.example.ui.components.DashboardHeader
import com.example.ui.components.MemoryStorageCard
import com.example.ui.components.MetricsHistoryChart
import com.example.ui.components.SystemInfoCard
import com.example.ui.theme.CyberDarkBg
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextMuted

@Composable
fun SystemMonitorScreen(
    viewModel: SystemMonitorViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val historyList by viewModel.metricsHistory.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = CyberDarkBg,
        contentWindowInsets = WindowInsets.systemBars
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = NeonCyan)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "INITIALIZING REAL-TIME DASHBOARD...",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }
            }
        } else {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + slideInVertically()
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .testTag("main_dashboard_scroll_view"),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .widthIn(max = 700.dp)
                        ) {
                            // TOP-LEFT: Live Time & TOP-RIGHT: Device Temperature Header
                            DashboardHeader(
                                liveTimeFormatted = uiState.liveTimeFormatted,
                                thermalInfo = uiState.thermal,
                                selectedTempUnit = uiState.selectedTempUnit,
                                onToggleTempUnit = { viewModel.toggleTemperatureUnit() }
                            )
                        }
                    }

                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .widthIn(max = 700.dp)
                        ) {
                            // CENTER: Battery Percentage Gauge & Just Below: Battery Health / Heat Status
                            BatteryCenterGauge(
                                battery = uiState.battery,
                                thermal = uiState.thermal
                            )
                        }
                    }

                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .widthIn(max = 700.dp)
                        ) {
                            // ADJACENT / SIDE SECTION: Clean Calendar Widget or Current Date View
                            CalendarWidget(
                                liveDateFormatted = uiState.liveDateFormatted
                            )
                        }
                    }

                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .widthIn(max = 700.dp)
                        ) {
                            // ACTIVE / RECENTLY OPENED APPS SECTION
                            ActiveAppsList(
                                activeApps = uiState.activeApps,
                                hasPermission = uiState.hasUsageStatsPermission
                            )
                        }
                    }

                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .widthIn(max = 700.dp)
                        ) {
                            // MEMORY & STORAGE USAGE CARDS
                            MemoryStorageCard(
                                memory = uiState.memory,
                                storage = uiState.storage,
                                onBoostMemory = { viewModel.boostMemory() }
                            )
                        }
                    }

                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .widthIn(max = 700.dp)
                        ) {
                            // METRICS HISTORY SNAPSHOTS (ROOM DB CHART)
                            MetricsHistoryChart(
                                historyList = historyList
                            )
                        }
                    }

                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .widthIn(max = 700.dp)
                        ) {
                            // SYSTEM ARCHITECTURE & UPTIME
                            SystemInfoCard(
                                systemInfo = uiState.system,
                                onRefresh = { viewModel.refreshManually() }
                            )
                        }
                    }
                }
            }
        }
    }
}
