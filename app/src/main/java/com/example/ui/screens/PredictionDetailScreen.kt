package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.StockViewModel
import com.example.ui.components.EnsembleBreakdownCard
import com.example.ui.components.ExchangeSwitchHeader
import com.example.ui.components.NewsSentimentFeed
import com.example.ui.components.TechnicalIndicatorsCard
import com.example.ui.components.VisualConfidenceCard
import com.example.ui.theme.BentoBorder
import com.example.ui.theme.BentoSubtleBorder
import com.example.ui.theme.BentoSurface
import com.example.ui.theme.BentoSurfaceVariant
import com.example.ui.theme.BearishRed
import com.example.ui.theme.BullishGreen
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun PredictionDetailScreen(
    viewModel: StockViewModel,
    modifier: Modifier = Modifier
) {
    val prediction by viewModel.prediction.collectAsState()
    val weightConfig by viewModel.modelWeights.collectAsState()
    val horizonDays by viewModel.horizonDays.collectAsState()
    val technicals by viewModel.technicals.collectAsState()
    val newsFeed by viewModel.newsFeed.collectAsState()
    val selectedExchange by viewModel.selectedExchange.collectAsState()
    val lastTickTimestamp by viewModel.lastTickTimestamp.collectAsState()
    val bidPrice by viewModel.bidPrice.collectAsState()
    val askPrice by viewModel.askPrice.collectAsState()
    val activeTicker by viewModel.activeTicker.collectAsState()
    val userApiKey by viewModel.userApiKey.collectAsState()
    val dataSourceStatus by viewModel.dataSourceStatus.collectAsState()
    val availableTickers = viewModel.availableTickers

    var searchQuery by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    var isSaved by remember { mutableStateOf(false) }

    val filteredTickers = remember(searchQuery) {
        if (searchQuery.isBlank()) availableTickers
        else availableTickers.filter {
            it.symbol.contains(searchQuery, ignoreCase = true) ||
                    it.companyName.contains(searchQuery, ignoreCase = true) ||
                    it.sector.contains(searchQuery, ignoreCase = true)
        }
    }

    LazyColumn(
        modifier = modifier
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

        // Target Stock Search & Selector Header Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = BentoSurface),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder),
                modifier = Modifier.fillMaxWidth().testTag("target_stock_search_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Target Stock Prediction Search",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it.uppercase() },
                        placeholder = { Text("Search any symbol (e.g. ZOMATO, TCS, BAJFINANCE, AAPL)...", fontSize = 12.sp, color = TextMuted) },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = PrimaryBlue, modifier = Modifier.size(18.dp))
                        },
                        trailingIcon = {
                            if (searchQuery.isNotBlank()) {
                                Button(
                                    onClick = {
                                        viewModel.selectTicker(searchQuery.trim().uppercase())
                                        keyboardController?.hide()
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.height(32.dp).padding(end = 4.dp)
                                ) {
                                    Text("Analyze", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = BentoSubtleBorder,
                            focusedContainerColor = BentoSurfaceVariant,
                            unfocusedContainerColor = BentoSurfaceVariant,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("prediction_stock_search_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Horizontal Quick Ticker Selection Chips
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(end = 8.dp)
                    ) {
                        items(filteredTickers) { ticker ->
                            val isSelected = ticker.symbol == activeTicker.symbol
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    viewModel.selectTicker(ticker.symbol)
                                    isSaved = false
                                    keyboardController?.hide()
                                },
                                label = {
                                    Text(
                                        text = ticker.symbol,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
                                    )
                                },
                                leadingIcon = {
                                    if (isSelected) {
                                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                                    }
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PrimaryBlue,
                                    selectedLabelColor = Color.White,
                                    containerColor = BentoSurfaceVariant,
                                    labelColor = TextMuted
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("target_chip_${ticker.symbol.lowercase()}")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Active Target Stock Highlight Bar
                    Surface(
                        color = BentoSurfaceVariant,
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BentoSubtleBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = activeTicker.symbol,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Black,
                                        color = TextPrimary
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        color = if (activeTicker.isLiveMarketData) BullishGreen.copy(alpha = 0.15f) else BentoSurfaceVariant,
                                        shape = RoundedCornerShape(6.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, if (activeTicker.isLiveMarketData) BullishGreen.copy(alpha = 0.4f) else BentoBorder)
                                    ) {
                                        Text(
                                            text = if (activeTicker.isLiveMarketData) "LIVE" else "CACHED",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (activeTicker.isLiveMarketData) BullishGreen else TextMuted,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        color = PrimaryBlue.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = activeTicker.exchange,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PrimaryBlue,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = activeTicker.companyName,
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "₹${String.format("%.2f", activeTicker.currentPrice)}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "${if (activeTicker.priceChange >= 0) "+" else ""}${String.format("%.2f", activeTicker.priceChange)} (${String.format("%.2f", activeTicker.priceChangePct)}%)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (activeTicker.priceChange >= 0) BullishGreen else BearishRed
                                )
                            }
                        }
                    }
                }
            }
        }

        // Ensemble Prediction Studio Card
        item {
            prediction?.let { pred ->
                EnsembleBreakdownCard(
                    prediction = pred,
                    weightConfig = weightConfig,
                    onWeightsChanged = { viewModel.setModelWeights(it) },
                    horizonDays = horizonDays,
                    onHorizonChanged = { viewModel.setHorizonDays(it) }
                )
            }
        }

        // Dynamic Visual Confidence Gauge Card
        item {
            prediction?.let { pred ->
                VisualConfidenceCard(prediction = pred)
            }
        }

        // Save Prediction to Local Room Database Button
        item {
            Button(
                onClick = {
                    viewModel.saveCurrentPrediction()
                    isSaved = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("save_prediction_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSaved) BullishGreen else BentoSurfaceVariant
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isSaved) Icons.Default.Check else Icons.Default.Bookmark,
                        contentDescription = null,
                        tint = if (isSaved) Color.White else PrimaryBlue
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isSaved) "Prediction Saved to History" else "Save Target Prediction to DB",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSaved) Color.White else PrimaryBlue
                    )
                }
            }
        }

        // Technical Indicators & Feature Engineering Card
        item {
            TechnicalIndicatorsCard(technicals = technicals)
        }

        // FinBERT News & Sentiment Feed
        item {
            NewsSentimentFeed(articles = newsFeed)
        }
    }
}

