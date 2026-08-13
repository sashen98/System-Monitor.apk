package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
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
import com.example.ui.theme.CyberCardBg
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberDarkBg
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

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
                Box(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .testTag("main_dashboard_scroll_view"),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // OVERHEAT WARNING BANNER
                        if (uiState.isOverheating) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .widthIn(max = 700.dp)
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(NeonRed.copy(alpha = 0.15f))
                                        .border(2.dp, NeonRed, RoundedCornerShape(20.dp))
                                        .padding(16.dp)
                                        .testTag("overheat_warning_banner")
                                ) {
                                    Column {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Warning,
                                                contentDescription = "Overheat Alert",
                                                tint = NeonRed,
                                                modifier = Modifier.size(24.dp)
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = "PHONE IS OVERHEATING!",
                                                    color = NeonRed,
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.Black
                                                )
                                                Text(
                                                    text = "Temperature reached ${String.format("%.1f", uiState.battery.tempCelsius)}°C. System notification sent.",
                                                    color = TextPrimary,
                                                    fontSize = 12.sp
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Button(
                                                onClick = {
                                                    viewModel.boostMemory()
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = NeonRed, contentColor = CyberDarkBg),
                                                shape = RoundedCornerShape(10.dp),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Speed,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("Cool Down & Optimize", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }

                                            OutlinedButton(
                                                onClick = { viewModel.toggleSimulateOverheat() },
                                                shape = RoundedCornerShape(10.dp),
                                                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextMuted)
                                            ) {
                                                Text("Reset", fontSize = 11.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }

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

                        // OVERHEAT TEST SIMULATOR ACTION BAR
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .widthIn(max = 700.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(CyberCardBg)
                                    .border(1.dp, CyberCardBorder, RoundedCornerShape(16.dp))
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.LocalFireDepartment,
                                            contentDescription = "Overheat Protection",
                                            tint = if (uiState.isOverheating) NeonRed else NeonAmber,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = if (uiState.isOverheating) "Overheat Mode Active" else "Overheat Protection Engine",
                                            color = if (uiState.isOverheating) NeonRed else TextPrimary,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    TextButton(
                                        onClick = { viewModel.toggleSimulateOverheat() }
                                    ) {
                                        Text(
                                            text = if (uiState.isSimulatedOverheat) "Stop Overheat Test" else "Test Overheat Alert",
                                            color = if (uiState.isSimulatedOverheat) NeonRed else NeonCyan,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
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

                    // BOTTOM STATUS SNACKBAR FOR FEEDBACK
                    uiState.statusMessage?.let { msg ->
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(innerPadding)
                                .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
                        ) {
                            Snackbar(
                                containerColor = CyberCardBg,
                                contentColor = TextPrimary,
                                modifier = Modifier
                                    .border(1.dp, NeonCyan, RoundedCornerShape(12.dp))
                                    .clip(RoundedCornerShape(12.dp)),
                                action = {
                                    IconButton(
                                        onClick = { viewModel.dismissStatusMessage() }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Dismiss",
                                            tint = NeonCyan,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            ) {
                                Text(
                                    text = msg,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonCyan
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
