package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.PredictionHistoryEntity
import com.example.data.local.StockDatabase
import com.example.data.local.StockEntity
import com.example.data.model.Candlestick
import com.example.data.model.EnsembleTargetPrediction
import com.example.data.model.GeminiNewsSentimentAnalysis
import com.example.data.model.ModelWeightConfig
import com.example.data.model.NewsSentimentArticle
import com.example.data.model.StockTicker
import com.example.data.model.TechnicalIndicators
import com.example.data.remote.DataSourceStatus
import com.example.data.remote.GeminiStockService
import com.example.data.remote.RealMarketApiService
import com.example.data.repository.StockRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class Exchange(val label: String, val code: String) {
    NSE("NSE", "NSE"),
    BSE("BSE", "BSE")
}

enum class ApiProvider(val displayName: String, val description: String) {
    YAHOO_FINANCE("Yahoo Finance API", "Real-time global equity quotes & TIME_SERIES_INTRADAY data"),
    ALPHA_VANTAGE("Alpha Vantage API", "Real-time global equity quotes & TIME_SERIES_DAILY data"),
    GEMINI_SERVER("Google Gemini AI Studio", "Server-side generative LLM market intelligence & target inferencing")
}

class StockViewModel(application: Application) : AndroidViewModel(application) {

    private val database = StockDatabase.getDatabase(application)
    private val realMarketApiService = RealMarketApiService()
    private val repository = StockRepository(database.stockDao(), realMarketApiService)
    private val geminiService = GeminiStockService()

    val availableTickers: List<StockTicker> = repository.getAvailableTickers()

    val watchlist: StateFlow<List<StockEntity>> = repository.watchlist
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedPredictions: StateFlow<List<PredictionHistoryEntity>> = repository.savedPredictions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedSymbol = MutableStateFlow("RELIANCE")
    val selectedSymbol: StateFlow<String> = _selectedSymbol.asStateFlow()

    private val _isUnlocked = MutableStateFlow(false)
    val isUnlocked: StateFlow<Boolean> = _isUnlocked.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _isLoadingStock = MutableStateFlow(false)
    val isLoadingStock: StateFlow<Boolean> = _isLoadingStock.asStateFlow()

    private val _userApiKey = MutableStateFlow("")
    val userApiKey: StateFlow<String> = _userApiKey.asStateFlow()

    private val _alphaVantageKey = MutableStateFlow("")
    val alphaVantageKey: StateFlow<String> = _alphaVantageKey.asStateFlow()

    private val _yahooFinanceKey = MutableStateFlow("")
    val yahooFinanceKey: StateFlow<String> = _yahooFinanceKey.asStateFlow()

    private val _isLiveApiMode = MutableStateFlow(true)
    val isLiveApiMode: StateFlow<Boolean> = _isLiveApiMode.asStateFlow()

    private val _dataSourceStatus = MutableStateFlow(DataSourceStatus.LIVE_MARKET_STREAM)
    val dataSourceStatus: StateFlow<DataSourceStatus> = _dataSourceStatus.asStateFlow()

    private val _selectedProvider = MutableStateFlow(ApiProvider.YAHOO_FINANCE)
    val selectedProvider: StateFlow<ApiProvider> = _selectedProvider.asStateFlow()

    private val _selectedExchange = MutableStateFlow(Exchange.NSE)
    val selectedExchange: StateFlow<Exchange> = _selectedExchange.asStateFlow()

    private val _lastTickTimestamp = MutableStateFlow("15:30:00 IST")
    val lastTickTimestamp: StateFlow<String> = _lastTickTimestamp.asStateFlow()

    private val _bidPrice = MutableStateFlow(2980.20)
    val bidPrice: StateFlow<Double> = _bidPrice.asStateFlow()

    private val _askPrice = MutableStateFlow(2980.50)
    val askPrice: StateFlow<Double> = _askPrice.asStateFlow()

    private val _horizonDays = MutableStateFlow(14)
    val horizonDays: StateFlow<Int> = _horizonDays.asStateFlow()

    private val _modelWeights = MutableStateFlow(ModelWeightConfig())
    val modelWeights: StateFlow<ModelWeightConfig> = _modelWeights.asStateFlow()

    private val _activeTicker = MutableStateFlow(repository.getFallbackTicker("RELIANCE"))
    val activeTicker: StateFlow<StockTicker> = _activeTicker.asStateFlow()

    private val _candles = MutableStateFlow<List<Candlestick>>(emptyList())
    val candles: StateFlow<List<Candlestick>> = _candles.asStateFlow()

    private val _technicals = MutableStateFlow(
        TechnicalIndicators(50f, 100f, 100f, 0f, 0f, 0f, 100f, 105f, 95f, 0)
    )
    val technicals: StateFlow<TechnicalIndicators> = _technicals.asStateFlow()

    private val _newsFeed = MutableStateFlow<List<NewsSentimentArticle>>(emptyList())
    val newsFeed: StateFlow<List<NewsSentimentArticle>> = _newsFeed.asStateFlow()

    private val _geminiNewsAnalysis = MutableStateFlow<GeminiNewsSentimentAnalysis?>(null)
    val geminiNewsAnalysis: StateFlow<GeminiNewsSentimentAnalysis?> = _geminiNewsAnalysis.asStateFlow()

    private val _isGeminiNewsLoading = MutableStateFlow(false)
    val isGeminiNewsLoading: StateFlow<Boolean> = _isGeminiNewsLoading.asStateFlow()

    private val _prediction = MutableStateFlow<EnsembleTargetPrediction?>(null)
    val prediction: StateFlow<EnsembleTargetPrediction?> = _prediction.asStateFlow()

    private val _aiReport = MutableStateFlow<String?>(null)
    val aiReport: StateFlow<String?> = _aiReport.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    private val _isWatchlisted = MutableStateFlow(false)
    val isWatchlisted: StateFlow<Boolean> = _isWatchlisted.asStateFlow()

    init {
        selectTicker("RELIANCE")
        startRealtimeTickerSimulation()
    }

    fun unlockApp() {
        _isUnlocked.value = true
    }

    fun lockApp() {
        _isUnlocked.value = false
    }

    fun refreshPrediction() {
        loadStockDataAsync(_selectedSymbol.value)
    }

    fun setApiKey(key: String) {
        _userApiKey.value = key
        _alphaVantageKey.value = key
        _yahooFinanceKey.value = key
        triggerGeminiNewsAnalysis()
        loadStockDataAsync(_selectedSymbol.value)
    }

    fun setAlphaVantageKey(key: String) {
        _alphaVantageKey.value = key
        if (key.isNotEmpty()) _userApiKey.value = key
        loadStockDataAsync(_selectedSymbol.value)
    }

    fun setYahooFinanceKey(key: String) {
        _yahooFinanceKey.value = key
        if (key.isNotEmpty()) _userApiKey.value = key
        loadStockDataAsync(_selectedSymbol.value)
    }

    fun setLiveApiMode(enabled: Boolean) {
        _isLiveApiMode.value = enabled
        loadStockDataAsync(_selectedSymbol.value)
    }

    fun setApiProvider(provider: ApiProvider) {
        _selectedProvider.value = provider
        loadStockDataAsync(_selectedSymbol.value)
    }

    fun setExchange(exchange: Exchange) {
        if (_selectedExchange.value == exchange) return
        _selectedExchange.value = exchange
        loadStockDataAsync(_selectedSymbol.value)
    }

    fun selectTicker(symbol: String) {
        _selectedSymbol.value = symbol.trim().uppercase()
        loadStockDataAsync(_selectedSymbol.value)
    }

    /**
     * Core Asynchronous Ingestion Pipeline with Multi-Tier Fallback
     */
    private fun loadStockDataAsync(symbol: String) {
        viewModelScope.launch {
            _isLoadingStock.value = true
            _isRefreshing.value = true
            val exchange = _selectedExchange.value
            val avKey = _alphaVantageKey.value

            val (ticker, candleList, status) = withContext(Dispatchers.IO) {
                repository.fetchTickerAndCandles(symbol, exchange.code, avKey)
            }

            _activeTicker.value = ticker
            _candles.value = candleList
            _dataSourceStatus.value = status
            _bidPrice.value = (ticker.currentPrice - 0.25).coerceAtLeast(0.5)
            _askPrice.value = (ticker.currentPrice + 0.25).coerceAtLeast(0.5)

            val tech = withContext(Dispatchers.Default) {
                repository.calculateTechnicalIndicators(candleList)
            }
            _technicals.value = tech

            val (newsArticles, _) = withContext(Dispatchers.IO) {
                repository.fetchNewsFeed(symbol)
            }
            _newsFeed.value = newsArticles

            recalculatePrediction()
            triggerGeminiNewsAnalysis()
            checkWatchlistStatus(symbol)
            _aiReport.value = null
            _isLoadingStock.value = false
            _isRefreshing.value = false
        }
    }

    fun triggerGeminiNewsAnalysis() {
        viewModelScope.launch {
            _isGeminiNewsLoading.value = true
            val symbol = _selectedSymbol.value
            val news = _newsFeed.value
            val apiKey = _userApiKey.value
            val isLive = _isLiveApiMode.value

            val result = geminiService.analyzeHeadlinesSentiment(
                symbol = symbol,
                articles = news,
                apiKeyOverride = apiKey,
                isLiveApiMode = isLive
            )
            _geminiNewsAnalysis.value = result
            _isGeminiNewsLoading.value = false
        }
    }

    fun setHorizonDays(days: Int) {
        _horizonDays.value = days
        recalculatePrediction()
    }

    fun setModelWeights(weights: ModelWeightConfig) {
        _modelWeights.value = weights
        recalculatePrediction()
    }

    private fun recalculatePrediction() {
        val symbol = _selectedSymbol.value
        val ticker = _activeTicker.value
        val candleList = _candles.value
        val weights = _modelWeights.value
        val horizon = _horizonDays.value
        val news = _newsFeed.value

        if (candleList.isNotEmpty()) {
            val pred = repository.computeMetaEnsembleTargetPrice(
                symbol = symbol,
                currentPrice = ticker.currentPrice,
                candles = candleList,
                weights = weights,
                horizonDays = horizon,
                newsFeed = news
            )
            _prediction.value = pred
        }
    }

    private fun checkWatchlistStatus(symbol: String) {
        viewModelScope.launch {
            _isWatchlisted.value = repository.isWatchlisted(symbol)
        }
    }

    fun toggleWatchlist() {
        viewModelScope.launch {
            val symbol = _selectedSymbol.value
            val ticker = _activeTicker.value
            if (_isWatchlisted.value) {
                repository.removeFromWatchlist(symbol)
                _isWatchlisted.value = false
            } else {
                repository.addToWatchlist(symbol, ticker.companyName, ticker.currentPrice * 1.1)
                _isWatchlisted.value = true
            }
        }
    }

    fun saveCurrentPrediction() {
        viewModelScope.launch {
            _prediction.value?.let { pred ->
                repository.savePrediction(pred)
            }
        }
    }

    fun deleteSavedPrediction(id: Long) {
        viewModelScope.launch {
            repository.deletePrediction(id)
        }
    }

    fun generateAiAnalysis() {
        viewModelScope.launch {
            _isAiLoading.value = true
            val symbol = _selectedSymbol.value
            val ticker = _activeTicker.value
            val tech = _technicals.value
            val horizon = _horizonDays.value

            val report = geminiService.analyzeStockWithAi(
                symbol = symbol,
                currentPrice = ticker.currentPrice,
                rsi = tech.rsi,
                macd = tech.macd,
                horizonDays = horizon,
                apiKeyOverride = _userApiKey.value
            )
            _aiReport.value = report
            _isAiLoading.value = false
        }
    }

    /**
     * Real-time WebSocket & Tick Stream Simulator
     */
    private fun startRealtimeTickerSimulation() {
        viewModelScope.launch {
            val sdf = SimpleDateFormat("HH:mm:ss 'IST'", Locale.US)
            while (isActive) {
                delay(3000)
                val current = _activeTicker.value
                val tickNoise = (Math.random() - 0.49) * (current.currentPrice * 0.0018)
                val newPrice = (current.currentPrice + tickNoise).coerceAtLeast(1.0)
                val newChange = current.priceChange + tickNoise
                val newChangePct = if (newPrice - newChange > 0) (newChange / (newPrice - newChange)) * 100.0 else 0.0

                val updatedTicker = current.copy(
                    currentPrice = newPrice,
                    priceChange = newChange,
                    priceChangePct = newChangePct
                )
                _activeTicker.value = updatedTicker
                _bidPrice.value = (newPrice - 0.25).coerceAtLeast(0.5)
                _askPrice.value = (newPrice + 0.25).coerceAtLeast(0.5)
                _lastTickTimestamp.value = sdf.format(Date())

                // Update last candle in memory
                val currentCandles = _candles.value.toMutableList()
                if (currentCandles.isNotEmpty()) {
                    val last = currentCandles.last()
                    val updatedLast = last.copy(
                        close = newPrice.toFloat(),
                        high = maxOf(last.high, newPrice.toFloat()),
                        low = minOf(last.low, newPrice.toFloat())
                    )
                    currentCandles[currentCandles.size - 1] = updatedLast
                    _candles.value = currentCandles

                    _technicals.value = repository.calculateTechnicalIndicators(currentCandles)
                    recalculatePrediction()
                }
            }
        }
    }
}
