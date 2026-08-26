package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TechnicalIndicators
import com.example.ui.theme.BearishRed
import com.example.ui.theme.BentoBorder
import com.example.ui.theme.BentoSurface
import com.example.ui.theme.BentoSurfaceVariant
import com.example.ui.theme.BullishGreen
import com.example.ui.theme.NeutralGold
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SecondaryCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun TechnicalIndicatorsCard(
    technicals: TechnicalIndicators,
    modifier: Modifier = Modifier
) {
    var showMathFormulas by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("technical_indicators_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = BentoSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(SecondaryCyan.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = null,
                            tint = SecondaryCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Technical Feature Engineering",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "RSI, MACD, EMA12/26 & Trend Signal",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                }

                Surface(
                    onClick = { showMathFormulas = !showMathFormulas },
                    color = BentoSurfaceVariant,
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (showMathFormulas) SecondaryCyan else BentoBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Functions,
                            contentDescription = "Math",
                            tint = SecondaryCyan,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Math",
                            fontSize = 11.sp,
                            color = SecondaryCyan,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Indicators Grid (2 x 2)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // RSI Item
                val rsiColor = when {
                    technicals.rsi < 30f -> BullishGreen
                    technicals.rsi > 70f -> BearishRed
                    else -> NeutralGold
                }
                IndicatorTile(
                    title = "RSI (14)",
                    value = String.format("%.1f", technicals.rsi),
                    status = when {
                        technicals.rsi < 30f -> "OVERSOLD"
                        technicals.rsi > 70f -> "OVERBOUGHT"
                        else -> "NEUTRAL"
                    },
                    statusColor = rsiColor,
                    modifier = Modifier.weight(1f)
                )

                // MACD Item
                val macdColor = if (technicals.macdHistogram >= 0f) BullishGreen else BearishRed
                IndicatorTile(
                    title = "MACD Histogram",
                    value = String.format("%.2f", technicals.macdHistogram),
                    status = if (technicals.macdHistogram >= 0f) "BULL CROSS" else "BEAR CROSS",
                    statusColor = macdColor,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // EMA 12/26 Item
                IndicatorTile(
                    title = "EMA 12 / 26",
                    value = "₹${String.format("%.1f", technicals.ema12)} / ₹${String.format("%.1f", technicals.ema26)}",
                    status = if (technicals.ema12 > technicals.ema26) "BULLISH ALIGN" else "BEARISH ALIGN",
                    statusColor = if (technicals.ema12 > technicals.ema26) BullishGreen else BearishRed,
                    modifier = Modifier.weight(1f)
                )

                // Trend Signal Item
                val (signalText, signalColor) = when (technicals.trendSignal) {
                    1 -> "BULLISH (+1)" to BullishGreen
                    -1 -> "BEARISH (-1)" to BearishRed
                    else -> "NEUTRAL (0)" to NeutralGold
                }
                IndicatorTile(
                    title = "XGBoost Trend Signal",
                    value = signalText,
                    status = "CLASSIFIER",
                    statusColor = signalColor,
                    modifier = Modifier.weight(1f)
                )
            }

            // Math Formulas View Popup
            if (showMathFormulas) {
                Spacer(modifier = Modifier.height(14.dp))
                Surface(
                    color = BentoSurfaceVariant,
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SecondaryCyan.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = SecondaryCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Mathematical Indicator Formulas", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SecondaryCyan)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "• Relative Strength Index (RSI):\n  RS = AvgGain / AvgLoss\n  RSI = 100 - (100 / (1 + RS))\n\n" +
                                   "• Exponential Moving Average (EMA):\n  EMA_t = (P_t * (2 / (N + 1))) + (EMA_{t-1} * (1 - (2 / (N + 1))))\n\n" +
                                   "• MACD Line = EMA_12(P) - EMA_26(P)\n" +
                                   "• Signal Line = EMA_9(MACD Line)\n" +
                                   "• Histogram = MACD Line - Signal Line",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun IndicatorTile(
    title: String,
    value: String,
    status: String,
    statusColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = BentoSurfaceVariant,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, fontSize = 11.sp, color = TextMuted, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(6.dp))
            Surface(
                color = statusColor.copy(alpha = 0.15f),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = status,
                    fontSize = 9.sp,
                    color = statusColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}
