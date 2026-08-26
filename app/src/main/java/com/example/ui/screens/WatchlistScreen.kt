package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.PredictionHistoryEntity
import com.example.data.local.StockEntity
import com.example.ui.StockViewModel
import com.example.ui.theme.BearishRed
import com.example.ui.theme.BentoBorder
import com.example.ui.theme.BentoSurface
import com.example.ui.theme.BentoSurfaceVariant
import com.example.ui.theme.BullishGreen
import com.example.ui.theme.NeutralGold
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WatchlistScreen(
    viewModel: StockViewModel,
    onNavigateToStock: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val watchlist by viewModel.watchlist.collectAsState()
    val savedPredictions by viewModel.savedPredictions.collectAsState()

    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section 1: Watchlist
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .background(PrimaryBlue.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Bookmark, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("NSE / BSE Watchlist (Room DB)", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }
        }

        if (watchlist.isEmpty()) {
            item {
                Surface(
                    color = BentoSurface,
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder),
                    shadowElevation = 2.dp
                ) {
                    Text(
                        text = "No saved Indian stocks in Watchlist yet. Tap the bookmark icon on any stock header to save it locally in Room Database.",
                        fontSize = 12.sp,
                        color = TextMuted,
                        modifier = Modifier.padding(18.dp)
                    )
                }
            }
        } else {
            items(watchlist, key = { it.symbol }) { stock ->
                WatchlistRowItem(
                    stock = stock,
                    onSelect = {
                        viewModel.selectTicker(stock.symbol)
                        onNavigateToStock(stock.symbol)
                    },
                    onDelete = { viewModel.toggleWatchlist() }
                )
            }
        }

        // Section 2: Saved Predictions History
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .background(BullishGreen.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.History, contentDescription = null, tint = BullishGreen, modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("Saved Target Prediction History", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }
        }

        if (savedPredictions.isEmpty()) {
            item {
                Surface(
                    color = BentoSurface,
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder),
                    shadowElevation = 2.dp
                ) {
                    Text(
                        text = "No saved predictions in history. Use 'Save Target Prediction' in the Prediction Studio to record model run snapshots.",
                        fontSize = 12.sp,
                        color = TextMuted,
                        modifier = Modifier.padding(18.dp)
                    )
                }
            }
        } else {
            items(savedPredictions, key = { it.id }) { item ->
                SavedPredictionRowItem(
                    prediction = item,
                    dateFormat = dateFormat,
                    onDelete = { viewModel.deleteSavedPrediction(item.id) },
                    onSelect = {
                        viewModel.selectTicker(item.symbol)
                        onNavigateToStock(item.symbol)
                    }
                )
            }
        }
    }
}

@Composable
private fun WatchlistRowItem(
    stock: StockEntity,
    onSelect: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        onClick = onSelect,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("watchlist_item_${stock.symbol}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = BentoSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(stock.symbol, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(stock.companyName, fontSize = 12.sp, color = TextMuted)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                stock.alertTargetPrice?.let { alert ->
                    Surface(color = BentoSurfaceVariant, shape = RoundedCornerShape(8.dp)) {
                        Text("Alert: ₹${String.format("%.1f", alert)}", fontSize = 11.sp, color = PrimaryBlue, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }

                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = BearishRed.copy(alpha = 0.8f))
                }
            }
        }
    }
}

@Composable
private fun SavedPredictionRowItem(
    prediction: PredictionHistoryEntity,
    dateFormat: SimpleDateFormat,
    onDelete: () -> Unit,
    onSelect: () -> Unit
) {
    val verdictColor = when {
        prediction.trendVerdict.contains("BULLISH") -> BullishGreen
        prediction.trendVerdict.contains("BEARISH") -> BearishRed
        else -> NeutralGold
    }

    Card(
        onClick = onSelect,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("saved_prediction_item_${prediction.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = BentoSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, verdictColor.copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(prediction.symbol, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(color = verdictColor.copy(alpha = 0.15f), shape = RoundedCornerShape(4.dp)) {
                        Text(prediction.trendVerdict, fontSize = 9.sp, color = verdictColor, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text("Saved: ${dateFormat.format(Date(prediction.savedAt))}", fontSize = 11.sp, color = TextMuted)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.End) {
                    Text("Target: ₹${String.format("%.2f", prediction.targetPrice)}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    val gainSign = if (prediction.targetGainPct >= 0) "+" else ""
                    Text("$gainSign${String.format("%.2f", prediction.targetGainPct)}%", fontSize = 11.sp, color = verdictColor, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = BearishRed.copy(alpha = 0.8f))
                }
            }
        }
    }
}
