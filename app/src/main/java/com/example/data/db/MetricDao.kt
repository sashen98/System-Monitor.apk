package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MetricDao {
    @Query("SELECT * FROM system_metrics_history ORDER BY timestamp DESC LIMIT 50")
    fun getRecentHistory(): Flow<List<MetricEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMetric(metric: MetricEntity)

    @Query("DELETE FROM system_metrics_history")
    suspend fun clearHistory()
}
