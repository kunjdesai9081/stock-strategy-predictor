package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StockTicker
import com.example.ui.StockViewModel
import com.example.ui.components.StockPriceChart
import com.example.ui.theme.AiPurple
import com.example.ui.theme.BearishRed
import com.example.ui.theme.BentoBorder
import com.example.ui.theme.BentoCardContainer
import com.example.ui.theme.BentoSubtleBorder
import com.example.ui.theme.BentoSurface
import com.example.ui.theme.BentoSurfaceVariant
import com.example.ui.theme.BullishGreen
import com.example.ui.theme.NeutralGold
import com.example.ui.theme.NseBlue
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SecondaryCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox

import com.example.ui.components.EnsembleBreakdownCard
import com.example.ui.components.ExchangeSwitchHeader
import com.example.ui.components.GeminiNewsSentimentCard
import com.example.ui.components.NewsSentimentFeed
import com.example.ui.components.StockPriceChart
import com.example.ui.components.TechnicalIndicatorsCard
import com.example.ui.components.VisualConfidenceCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: StockViewModel,
    onNavigateToPrediction: () -> Unit,
    onNavigateToAiDeepDive: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedSymbol by viewModel.selectedSymbol.collectAsState()
    val selectedExchange by viewModel.selectedExchange.collectAsState()
    val lastTickTimestamp by viewModel.lastTickTimestamp.collectAsState()
    val bidPrice by viewModel.bidPrice.collectAsState()
    val askPrice by viewModel.askPrice.collectAsState()
    val activeTicker by viewModel.activeTicker.collectAsState()
    val candles by viewModel.candles.collectAsState()
    val technicals by viewModel.technicals.collectAsState()
    val prediction by viewModel.prediction.collectAsState()
    val isWatchlisted by viewModel.isWatchlisted.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val userApiKey by viewModel.userApiKey.collectAsState()
    val isLiveApiMode by viewModel.isLiveApiMode.collectAsState()
    val dataSourceStatus by viewModel.dataSourceStatus.collectAsState()
    val isLoadingStock by viewModel.isLoadingStock.collectAsState()
    val geminiNewsAnalysis by viewModel.geminiNewsAnalysis.collectAsState()
    val isGeminiNewsLoading by viewModel.isGeminiNewsLoading.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { viewModel.refreshPrediction() },
        modifier = modifier
            .fillMaxSize()
            .testTag("dashboard_pull_to_refresh")
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Exchange Switcher Bar & Live Telemetry
            item {
                ExchangeSwitchHeader(
                    selectedExchange = selectedExchange,
                    onExchangeSelected = { viewModel.setExchange(it) },
                    lastTickTimestamp = lastTickTimestamp,
                    bidPrice = bidPrice,
                    askPrice = askPrice,
                    apiKey = userApiKey,
                    dataSourceStatus = dataSourceStatus,
                    onApiKeySaved = { viewModel.setApiKey(it) }
                )
            }

            // Stock Lookup & AI Prediction Trigger Bar
            item {
                Surface(
                    color = BentoSurface,
                    shape = RoundedCornerShape(22.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder),
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth().testTag("stock_search_bar_card")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it.uppercase() },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("stock_search_input"),
                                placeholder = {
                                    Text("Search symbol (e.g. RELIANCE)...", fontSize = 12.sp, color = TextMuted)
                                },
                            leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = "Search", tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextMuted, modifier = Modifier.size(18.dp))
                                    }
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = BentoSurfaceVariant,
                                unfocusedContainerColor = BentoSurfaceVariant,
                                focusedBorderColor = PrimaryBlue,
                                unfocusedBorderColor = BentoSubtleBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(
                                onSearch = {
                                    if (searchQuery.isNotBlank()) {
                                        viewModel.selectTicker(searchQuery.trim().uppercase())
                                        keyboardController?.hide()
                                    }
                                }
                            )
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                if (searchQuery.isNotBlank()) {
                                    viewModel.selectTicker(searchQuery.trim().uppercase())
                                    keyboardController?.hide()
                                } else {
                                    viewModel.selectTicker(selectedSymbol)
                                }
                            },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 14.dp),
                            modifier = Modifier.testTag("stock_search_button")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Predict", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Matching stock suggestions from database/repository
                    val matchingTickers = if (searchQuery.isNotBlank()) {
                        viewModel.availableTickers.filter {
                            it.symbol.contains(searchQuery, ignoreCase = true) || it.companyName.contains(searchQuery, ignoreCase = true)
                        }
                    } else emptyList()

                    if (matchingTickers.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("MATCHING SYMBOLS", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                        Spacer(modifier = Modifier.height(4.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(matchingTickers) { ticker ->
                                Surface(
                                    onClick = {
                                        searchQuery = ticker.symbol
                                        viewModel.selectTicker(ticker.symbol)
                                        keyboardController?.hide()
                                    },
                                    color = if (ticker.symbol == selectedSymbol) PrimaryBlue.copy(alpha = 0.15f) else BentoSurfaceVariant,
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (ticker.symbol == selectedSymbol) PrimaryBlue else BentoSubtleBorder
                                    )
                                ) {
                                    Text(
                                        text = "${ticker.symbol} (${ticker.companyName})",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (ticker.symbol == selectedSymbol) PrimaryBlue else TextSecondary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Ticker Carousel Bar
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("INDIAN MARKET STREAM", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                    Text("NSE / BSE LIVE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NseBlue)
                }
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp)
                ) {
                    items(viewModel.availableTickers) { ticker ->
                        TickerChipItem(
                            ticker = ticker,
                            isSelected = ticker.symbol == selectedSymbol,
                            onClick = { viewModel.selectTicker(ticker.symbol) }
                        )
                    }
                }
            }
        }

        // Main Selected Stock Bento Tile
        item {
            Surface(
                color = BentoSurface,
                shape = RoundedCornerShape(24.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder),
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth().testTag("selected_stock_header_card")
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.horizontalScroll(rememberScrollState())
                            ) {
                                Text(
                                    text = activeTicker.symbol,
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    color = if (activeTicker.isLiveMarketData) BullishGreen.copy(alpha = 0.15f) else NeutralGold.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (activeTicker.isLiveMarketData) BullishGreen.copy(alpha = 0.4f) else NeutralGold.copy(alpha = 0.6f))
                                ) {
                                    Text(
                                        text = if (activeTicker.isLiveMarketData) "LIVE QUOTE" else "OFFLINE MODE",
                                        fontSize = 9.sp,
                                        color = if (activeTicker.isLiveMarketData) BullishGreen else NeutralGold,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = NseBlue.copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = activeTicker.exchange,
                                        fontSize = 10.sp,
                                        color = NseBlue,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = BentoSurfaceVariant,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = activeTicker.sector,
                                        fontSize = 10.sp,
                                        color = PrimaryBlue,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                            Text(
                                text = activeTicker.companyName,
                                fontSize = 13.sp,
                                color = TextMuted,
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                            )
                        }

                        IconButton(
                            onClick = { viewModel.toggleWatchlist() },
                            modifier = Modifier.testTag("watchlist_toggle_button")
                        ) {
                            Icon(
                                imageVector = if (isWatchlisted) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Watchlist",
                                tint = if (isWatchlisted) PrimaryBlue else TextMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text("LIVE NSE/BSE PRICE", fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                            Text(
                                text = "₹${String.format("%.2f", activeTicker.currentPrice)}",
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary
                            )
                        }

                        val isPositive = activeTicker.priceChange >= 0
                        val changeColor = if (isPositive) BullishGreen else BearishRed
                        Surface(
                            color = changeColor.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isPositive) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                                    contentDescription = null,
                                    tint = changeColor,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                val sign = if (isPositive) "+" else ""
                                Text(
                                    text = "$sign₹${String.format("%.2f", activeTicker.priceChange)} (${String.format("%.2f", activeTicker.priceChangePct)}%)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = changeColor
                                )
                            }
                        }
                    }
                }
            }
        }

        // Live Canvas Chart Bento Tile
        item {
            StockPriceChart(candles = candles, technicals = technicals)
        }

        // Quick Prediction Banner Bento Card
        item {
            prediction?.let { pred ->
                val verdictColor = when {
                    pred.trendVerdict.contains("STRONG BULLISH") -> BullishGreen
                    pred.trendVerdict.contains("BULLISH") -> BullishGreen
                    pred.trendVerdict.contains("BEARISH") -> BearishRed
                    else -> NeutralGold
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToPrediction() }
                        .testTag("quick_prediction_banner"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = BentoSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, verdictColor.copy(alpha = 0.4f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(AiPurple.copy(alpha = 0.15f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AiPurple, modifier = Modifier.size(16.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("AI Target Price Verdict", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Full Studio", fontSize = 12.sp, color = PrimaryBlue, fontWeight = FontWeight.Bold)
                                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Surface(
                                    color = verdictColor.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "${pred.targetHorizonDays}-DAY ${pred.trendVerdict}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = verdictColor,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "₹${String.format("%.2f", pred.targetPrice)}",
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Black,
                                    color = TextPrimary
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                val gainSign = if (pred.targetGainPct >= 0) "+" else ""
                                Text(
                                    text = "$gainSign${String.format("%.2f", pred.targetGainPct)}%",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = verdictColor
                                )
                                Text(
                                    text = "Confidence: ${String.format("%.0f", pred.confidenceScorePct)}%",
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }
                        }
                    }
                }
            }
        }

        // Dynamic Visual Model Confidence Gauge Card
        item {
            prediction?.let { pred ->
                VisualConfidenceCard(prediction = pred)
            }
        }

        // Gemini AI News Sentiment Component
        item {
            GeminiNewsSentimentCard(
                analysis = geminiNewsAnalysis,
                isLoading = isGeminiNewsLoading,
                isLiveApiMode = isLiveApiMode,
                onReAnalyze = { viewModel.triggerGeminiNewsAnalysis() },
                onNavigateToSettings = { onNavigateToPrediction() }
            )
        }

        // Quick AI Deep Dive Button
        item {
            Button(
                onClick = onNavigateToAiDeepDive,
                modifier = Modifier.fillMaxWidth().height(54.dp).testTag("quick_ai_deep_dive_button"),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Generate AI Dalal Street Report", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
}

@Composable
private fun TickerChipItem(
    ticker: StockTicker,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val isPositive = ticker.priceChange >= 0
    val color = if (isPositive) BullishGreen else BearishRed

    Surface(
        onClick = onClick,
        color = if (isSelected) BentoCardContainer else BentoSurface,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) PrimaryBlue else BentoBorder),
        shadowElevation = if (isSelected) 3.dp else 1.dp,
        modifier = Modifier.testTag("ticker_chip_${ticker.symbol}")
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(ticker.symbol, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.width(6.dp))
                val sign = if (isPositive) "+" else ""
                Text("$sign${String.format("%.1f", ticker.priceChangePct)}%", fontSize = 10.sp, color = color, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text("₹${String.format("%.2f", ticker.currentPrice)}", fontSize = 11.sp, color = TextSecondary)
        }
    }
}
