package com.example

import com.example.data.local.StockDao
import com.example.data.local.StockEntity
import com.example.data.local.PredictionHistoryEntity
import com.example.data.model.Candlestick
import com.example.data.model.ModelWeightConfig
import com.example.data.model.NewsSentimentArticle
import com.example.data.repository.StockRepository
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ExampleUnitTest {

    private lateinit var fakeDao: FakeStockDao
    private lateinit var repository: StockRepository

    @Before
    fun setUp() {
        fakeDao = FakeStockDao()
        repository = StockRepository(fakeDao)
    }

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testTechnicalIndicatorsCalculation() {
        val now = System.currentTimeMillis()
        val candles = (1..30).map { i ->
            val price = 1000f + (i * 5f)
            Candlestick(
                timestamp = now + (i * 86400000L),
                open = price - 2f,
                high = price + 6f,
                low = price - 4f,
                close = price,
                volume = 1000000L
            )
        }

        val technicals = repository.calculateTechnicalIndicators(candles)

        // RSI should be valid between 0 and 100
        assertTrue("RSI must be between 0 and 100", technicals.rsi in 0f..100f)
        assertTrue("EMA12 must be positive", technicals.ema12 > 0f)
        assertTrue("EMA26 must be positive", technicals.ema26 > 0f)
        assertTrue("Upper Bollinger must be >= Lower Bollinger", technicals.upperBollinger >= technicals.lowerBollinger)
    }

    @Test
    fun testLstmForecastGeneration() {
        val now = System.currentTimeMillis()
        val candles = (1..60).map { i ->
            Candlestick(
                timestamp = now + (i * 86400000L),
                open = 2500f,
                high = 2550f,
                low = 2480f,
                close = 2520f,
                volume = 5000000L
            )
        }

        val forecast7d = repository.calculateLstmForecast(candles, 7)
        assertEquals(7, forecast7d.forecastPrices.size)
        assertEquals(7, forecast7d.upperBound.size)
        assertEquals(7, forecast7d.lowerBound.size)

        val forecast14d = repository.calculateLstmForecast(candles, 14)
        assertEquals(14, forecast14d.forecastPrices.size)
    }

    @Test
    fun testMetaEnsembleTargetPriceMath() {
        val now = System.currentTimeMillis()
        val candles = (1..60).map { i ->
            Candlestick(
                timestamp = now + (i * 86400000L),
                open = 1500f,
                high = 1530f,
                low = 1490f,
                close = 1515f,
                volume = 8000000L
            )
        }

        val news = listOf(
            NewsSentimentArticle("1", "Positive order growth", "Reuters", "1h ago", 0.75f, "BULLISH", "Strong demand")
        )

        val prediction = repository.computeMetaEnsembleTargetPrice(
            symbol = "INFY",
            currentPrice = 1515.0,
            candles = candles,
            weights = ModelWeightConfig(0.45f, 0.45f, 0.10f),
            horizonDays = 14,
            newsFeed = news
        )

        assertEquals("INFY", prediction.ticker)
        assertTrue("Target price must be positive", prediction.targetPrice > 0)
        assertTrue("Confidence score must be >= 50%", prediction.confidenceScorePct >= 50f)
        assertNotNull(prediction.trendVerdict)
        assertEquals(3, prediction.keyCatalysts.size)
    }

    @Test
    fun testFallbackTickerResolution() {
        val fallbackReliance = repository.getFallbackTicker("RELIANCE")
        assertEquals("RELIANCE", fallbackReliance.symbol)
        assertTrue(fallbackReliance.currentPrice > 0)

        val fallbackCustom = repository.getFallbackTicker("CUSTOMCO")
        assertEquals("CUSTOMCO", fallbackCustom.symbol)
        assertFalse(fallbackCustom.isLiveMarketData)
    }
}

private class FakeStockDao : StockDao {
    private val watchlist = mutableListOf<StockEntity>()
    private val history = mutableListOf<PredictionHistoryEntity>()

    override fun getWatchlist() = flowOf(watchlist.toList())

    override suspend fun insertWatchlistStock(stock: StockEntity) {
        watchlist.removeAll { it.symbol == stock.symbol }
        watchlist.add(stock)
    }

    override suspend fun deleteWatchlistStock(symbol: String) {
        watchlist.removeAll { it.symbol == symbol }
    }

    override suspend fun isInWatchlist(symbol: String): Boolean {
        return watchlist.any { it.symbol == symbol }
    }

    override fun getPredictionHistory() = flowOf(history.toList())

    override suspend fun savePrediction(prediction: PredictionHistoryEntity) {
        history.add(prediction)
    }

    override suspend fun deletePrediction(id: Long) {
        history.removeAll { it.id == id }
    }
}
