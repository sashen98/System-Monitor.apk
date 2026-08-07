package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "system_metrics_history")
data class MetricEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val batteryLevel: Int,
    val tempCelsius: Float,
    val ramUsagePercent: Int,
    val storageUsagePercent: Int,
    val batteryHealth: String
)
