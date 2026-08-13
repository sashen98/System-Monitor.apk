package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.repository.SystemMonitorRepository
import com.example.data.db.MetricEntity
import com.example.model.SystemMetricsState
import com.example.model.TemperatureUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import com.example.util.NotificationHelper

class SystemMonitorViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = SystemMonitorRepository(application, db.metricDao())

    private val _uiState = MutableStateFlow(SystemMetricsState())
    val uiState: StateFlow<SystemMetricsState> = _uiState.asStateFlow()

    val metricsHistory: StateFlow<List<com.example.data.db.MetricEntity>> = repository.getMetricsHistory()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    private val dateFormat = SimpleDateFormat("EEEE, MMM d, yyyy", Locale.getDefault())

    private var snapshotCounter = 0
    private var notificationSentForCurrentEpisode = false

    init {
        NotificationHelper.createNotificationChannel(application)
        startRealtimeMonitoring()
    }

    fun startRealtimeMonitoring() {
        viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                refreshMetricsInternal()
                delay(1000L) // Update every second for live clock & device stats
            }
        }
    }

    private suspend fun refreshMetricsInternal() {
        val now = Date()
        val formattedTime = timeFormat.format(now)
        val formattedDate = dateFormat.format(now)

        val rawBattery = repository.getBatteryInfo()
        val currentState = _uiState.value

        // Adjust battery temp if simulated overheat is on
        val effectiveTemp = if (currentState.isSimulatedOverheat) {
            43.5f
        } else {
            rawBattery.tempCelsius
        }

        val battery = if (currentState.isSimulatedOverheat) {
            rawBattery.copy(tempCelsius = effectiveTemp, healthStatus = "Overheat")
        } else {
            rawBattery
        }

        val thermal = repository.getThermalInfo(effectiveTemp)
        val memory = repository.getMemoryInfo()
        val storage = repository.getStorageInfo()
        val system = repository.getSystemInfo()
        val hasUsagePermission = repository.hasUsageStatsPermission()
        val recentApps = repository.getRecentApps()

        val isOverheatDetected = effectiveTemp >= 40f || thermal.statusLevel >= 2 || battery.healthStatus == "Overheat"

        if (isOverheatDetected && !notificationSentForCurrentEpisode) {
            notificationSentForCurrentEpisode = true
            NotificationHelper.showOverheatNotification(getApplication(), effectiveTemp)
        } else if (!isOverheatDetected) {
            notificationSentForCurrentEpisode = false
        }

        _uiState.update { current ->
            current.copy(
                liveTimeFormatted = formattedTime,
                liveDateFormatted = formattedDate,
                battery = battery,
                thermal = thermal,
                memory = memory.copy(isOptimizing = current.memory.isOptimizing),
                storage = storage,
                system = system,
                activeApps = recentApps,
                hasUsageStatsPermission = hasUsagePermission,
                isOverheating = isOverheatDetected,
                isLoading = false
            )
        }

        // Save snapshot to Room DB every 30 seconds
        snapshotCounter++
        if (snapshotCounter >= 30) {
            snapshotCounter = 0
            repository.saveSnapshot(
                batteryLevel = battery.level,
                tempCelsius = battery.tempCelsius,
                ramUsagePct = memory.usagePercentage,
                storageUsagePct = storage.usagePercentage,
                batteryHealth = battery.healthStatus
            )
        }
    }

    fun toggleTemperatureUnit() {
        _uiState.update { current ->
            val nextUnit = if (current.selectedTempUnit == TemperatureUnit.CELSIUS) {
                TemperatureUnit.FAHRENHEIT
            } else {
                TemperatureUnit.CELSIUS
            }
            current.copy(selectedTempUnit = nextUnit)
        }
    }

    fun toggleSimulateOverheat() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { current ->
                val nextSimulated = !current.isSimulatedOverheat
                current.copy(
                    isSimulatedOverheat = nextSimulated,
                    statusMessage = if (nextSimulated) "Simulated Overheat Activated! Check Notification." else "Normal Temperature Restored"
                )
            }
            if (_uiState.value.isSimulatedOverheat) {
                notificationSentForCurrentEpisode = false
            }
            refreshMetricsInternal()
        }
    }

    fun dismissStatusMessage() {
        _uiState.update { it.copy(statusMessage = null) }
    }

    fun refreshManually() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true) }
            refreshMetricsInternal()
            val state = _uiState.value
            repository.saveSnapshot(
                batteryLevel = state.battery.level,
                tempCelsius = state.battery.tempCelsius,
                ramUsagePct = state.memory.usagePercentage,
                storageUsagePct = state.storage.usagePercentage,
                batteryHealth = state.battery.healthStatus
            )
        }
    }

    fun boostMemory() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { current ->
                current.copy(memory = current.memory.copy(isOptimizing = true))
            }
            
            delay(500L) // Show optimizing animation
            val freedMb = repository.boostAndCleanMemory()
            refreshMetricsInternal()

            _uiState.update { current ->
                current.copy(
                    memory = current.memory.copy(
                        isOptimizing = false,
                        lastFreedMb = freedMb,
                        isOptimized = true
                    ),
                    statusMessage = "Memory Optimized! Freed $freedMb MB RAM."
                )
            }
        }
    }
}
