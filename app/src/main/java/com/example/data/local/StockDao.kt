package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StockDao {
    @Query("SELECT * FROM watchlist_stocks ORDER BY addedAt DESC")
    fun getWatchlist(): Flow<List<StockEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWatchlistStock(stock: StockEntity)

    @Query("DELETE FROM watchlist_stocks WHERE symbol = :symbol")
    suspend fun deleteWatchlistStock(symbol: String)

    @Query("SELECT EXISTS(SELECT 1 FROM watchlist_stocks WHERE symbol = :symbol)")
    suspend fun isInWatchlist(symbol: String): Boolean

    @Query("SELECT * FROM prediction_history ORDER BY savedAt DESC")
    fun getPredictionHistory(): Flow<List<PredictionHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePrediction(prediction: PredictionHistoryEntity)

    @Query("DELETE FROM prediction_history WHERE id = :id")
    suspend fun deletePrediction(id: Long)
}
