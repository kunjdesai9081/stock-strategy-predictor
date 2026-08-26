package com.example.data.model

data class StockTicker(
    val symbol: String,
    val companyName: String,
    val currentPrice: Double,
    val priceChange: Double,
    val priceChangePct: Double,
    val volume: Long,
    val marketCap: String,
    val sector: String,
    val dayHigh: Double,
    val dayLow: Double,
    val exchange: String = "NSE",
    val isLiveMarketData: Boolean = false
)

data class Candlestick(
    val timestamp: Long,
    val open: Float,
    val high: Float,
    val low: Float,
    val close: Float,
    val volume: Long
)

data class TechnicalIndicators(
    val rsi: Float,
    val ema12: Float,
    val ema26: Float,
    val macd: Float,
    val macdSignal: Float,
    val macdHistogram: Float,
    val sma20: Float,
    val upperBollinger: Float,
    val lowerBollinger: Float,
    val trendSignal: Int // -1: Bearish, 0: Neutral, 1: Bullish
)

data class NewsSentimentArticle(
    val id: String,
    val headline: String,
    val source: String,
    val publishedTime: String,
    val sentimentScore: Float, // -1.0 to 1.0
    val sentimentLabel: String, // BULLISH, BEARISH, NEUTRAL
    val snippet: String
)

data class LstmForecast(
    val horizonDays: Int,
    val forecastPrices: List<Float>,
    val upperBound: List<Float>,
    val lowerBound: List<Float>,
    val predictedReturnPct: Float
)

data class ModelWeightConfig(
    val w1Lstm: Float = 0.45f,
    val w2Xgboost: Float = 0.45f,
    val alphaFinbert: Float = 0.10f
)

data class EnsembleTargetPrediction(
    val ticker: String,
    val currentPrice: Double,
    val targetPrice: Double,
    val targetHorizonDays: Int,
    val targetGainPct: Double,
    val confidenceScorePct: Float,
    val trendVerdict: String, // STRONG BULLISH, BULLISH, NEUTRAL, BEARISH, STRONG BEARISH
    val lstmPrice: Double,
    val xgboostPrice: Double,
    val finbertScore: Float,
    val keyCatalysts: List<String>,
    val timestamp: Long = System.currentTimeMillis()
)

data class HeadlineSentimentItem(
    val headline: String,
    val sentiment: String, // "BULLISH", "BEARISH", "NEUTRAL"
    val score: Float,      // -1.0 to 1.0
    val explanation: String
)

data class GeminiNewsSentimentAnalysis(
    val symbol: String,
    val overallSentiment: String, // "BULLISH", "BEARISH", "NEUTRAL"
    val sentimentScore: Float,    // -1.0 to +1.0
    val confidencePct: Int,       // 0 to 100
    val rationaleSummary: String,
    val items: List<HeadlineSentimentItem>,
    val isLiveApi: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
