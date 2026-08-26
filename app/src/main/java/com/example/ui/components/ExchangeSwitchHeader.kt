package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CellTower
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.Exchange
import com.example.ui.theme.BearishRed
import com.example.ui.theme.BentoBorder
import com.example.ui.theme.BentoSubtleBorder
import com.example.ui.theme.BentoSurface
import com.example.ui.theme.BentoSurfaceVariant
import com.example.ui.theme.BullishGreen
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun ExchangeSwitchHeader(
    selectedExchange: Exchange,
    onExchangeSelected: (Exchange) -> Unit,
    lastTickTimestamp: String,
    bidPrice: Double,
    askPrice: Double,
    apiKey: String = "",
    dataSourceStatus: com.example.data.remote.DataSourceStatus = com.example.data.remote.DataSourceStatus.LIVE_MARKET_STREAM,
    onApiKeySaved: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alphaPulse by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    var showApiKeyDialog by remember { mutableStateOf(false) }
    var inputApiKey by remember(apiKey) { mutableStateOf(apiKey) }

    if (showApiKeyDialog) {
        AlertDialog(
            onDismissRequest = { showApiKeyDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Key, contentDescription = null, tint = PrimaryBlue)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("API Configuration", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
            },
            text = {
                Column {
                    Text(
                        "Add your custom Market Data or AI API Key (Alpha Vantage / RapidAPI / Gemini) to stream live prices and custom inferences directly.",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value = inputApiKey,
                        onValueChange = { inputApiKey = it },
                        label = { Text("API Key / Token", fontSize = 12.sp) },
                        placeholder = { Text("e.g. c1234567890abcdef...", fontSize = 12.sp, color = TextMuted) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("api_key_dialog_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onApiKeySaved(inputApiKey)
                        showApiKeyDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text("Save API Key", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showApiKeyDialog = false }) {
                    Text("Cancel", color = TextMuted)
                }
            },
            containerColor = BentoSurface,
            shape = RoundedCornerShape(20.dp)
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("exchange_switch_header")
    ) {
        Surface(
            color = BentoSurface,
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder),
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                // Row 1: Exchange Switcher [ NSE | BSE ] & Live Pulse Tag
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // NSE / BSE Segmented Button Switch
                    Surface(
                        color = BentoSurfaceVariant,
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BentoSubtleBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Exchange.values().forEach { exchange ->
                                val isSelected = selectedExchange == exchange
                                Box(
                                    modifier = Modifier
                                        .background(
                                            color = if (isSelected) PrimaryBlue else BentoSurfaceVariant,
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .clickable { onExchangeSelected(exchange) }
                                        .padding(horizontal = 14.dp, vertical = 6.dp)
                                        .testTag("exchange_toggle_${exchange.name.lowercase()}"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = exchange.label,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) BentoSurface else TextMuted
                                    )
                                }
                            }
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            onClick = { showApiKeyDialog = true },
                            color = if (apiKey.isNotEmpty()) PrimaryBlue.copy(alpha = 0.15f) else BentoSurfaceVariant,
                            shape = CircleShape,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (apiKey.isNotEmpty()) PrimaryBlue else BentoSubtleBorder),
                            modifier = Modifier.testTag("api_key_config_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Key,
                                    contentDescription = "API Key Config",
                                    tint = if (apiKey.isNotEmpty()) PrimaryBlue else TextMuted,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (apiKey.isNotEmpty()) "API KEY" else "+ KEY",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (apiKey.isNotEmpty()) PrimaryBlue else TextMuted
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Dynamic Real-Time Stream Status Badge
                        val badgeColor = when (dataSourceStatus) {
                            com.example.data.remote.DataSourceStatus.LIVE_MARKET_STREAM -> BullishGreen
                            com.example.data.remote.DataSourceStatus.ALPHA_VANTAGE_API -> PrimaryBlue
                            com.example.data.remote.DataSourceStatus.OFFLINE_CACHED_FALLBACK -> com.example.ui.theme.NeutralGold
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(badgeColor.copy(alpha = 0.12f), CircleShape)
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .alpha(if (dataSourceStatus.isLive) alphaPulse else 1f)
                                    .background(badgeColor, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = dataSourceStatus.label,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = badgeColor
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Row 2: Live L1 Orderbook Telemetry (Timestamp, Bid, Ask)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CellTower,
                            contentDescription = "Live Tick",
                            tint = PrimaryBlue,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Tick: $lastTickTimestamp",
                            fontSize = 11.sp,
                            color = TextMuted,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Bid: ₹${String.format("%.2f", bidPrice)}",
                            fontSize = 11.sp,
                            color = BullishGreen,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Ask: ₹${String.format("%.2f", askPrice)}",
                            fontSize = 11.sp,
                            color = BearishRed,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
