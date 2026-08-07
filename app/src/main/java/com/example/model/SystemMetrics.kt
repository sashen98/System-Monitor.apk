package com.example.model

import android.graphics.drawable.Drawable

data class BatteryInfo(
    val level: Int = 0,
    val isCharging: Boolean = false,
    val healthStatus: String = "Good",
    val healthCode: Int = 2,
    val tempCelsius: Float = 0f,
    val voltage: Int = 0,
    val technology: String = "Li-ion",
    val plugType: String = "Discharging"
)

data class ThermalInfo(
    val status: String = "Normal",
    val statusLevel: Int = 0, // 0 = Normal, 1 = Light, 2 = Moderate, 3 = Severe, 4 = Critical
    val tempCelsius: Float = 0f
)

data class MemoryInfo(
    val totalRamBytes: Long = 0L,
    val availableRamBytes: Long = 0L,
    val usedRamBytes: Long = 0L,
    val usagePercentage: Int = 0,
    val isLowMemory: Boolean = false
)

data class StorageInfo(
    val totalStorageBytes: Long = 0L,
    val freeStorageBytes: Long = 0L,
    val usedStorageBytes: Long = 0L,
    val usagePercentage: Int = 0
)

data class AppUsageInfo(
    val packageName: String,
    val appName: String,
    val iconDrawable: Drawable? = null,
    val lastTimeUsed: Long = 0L,
    val totalTimeForegroundMs: Long = 0L,
    val formattedLastUsed: String = ""
)

data class SystemInfo(
    val deviceName: String = "",
    val model: String = "",
    val manufacturer: String = "",
    val osVersion: String = "",
    val sdkInt: Int = 0,
    val cpuCores: Int = 1,
    val uptimeMillis: Long = 0L,
    val formattedUptime: String = ""
)

enum class TemperatureUnit {
    CELSIUS, FAHRENHEIT
}

data class SystemMetricsState(
    val liveTimeFormatted: String = "",
    val liveDateFormatted: String = "",
    val battery: BatteryInfo = BatteryInfo(),
    val thermal: ThermalInfo = ThermalInfo(),
    val memory: MemoryInfo = MemoryInfo(),
    val storage: StorageInfo = StorageInfo(),
    val system: SystemInfo = SystemInfo(),
    val activeApps: List<AppUsageInfo> = emptyList(),
    val hasUsageStatsPermission: Boolean = false,
    val selectedTempUnit: TemperatureUnit = TemperatureUnit.CELSIUS,
    val isLoading: Boolean = true
)
