package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watchlist_stocks")
data class StockEntity(
    @PrimaryKey val symbol: String,
    val companyName: String,
    val alertTargetPrice: Double? = null,
    val isAlertEnabled: Boolean = false,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "prediction_history")
data class PredictionHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val symbol: String,
    val currentPrice: Double,
    val targetPrice: Double,
    val horizonDays: Int,
    val targetGainPct: Double,
    val confidencePct: Float,
    val trendVerdict: String,
    val savedAt: Long = System.currentTimeMillis()
)
