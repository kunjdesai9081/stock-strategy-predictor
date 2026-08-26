package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EnsembleTargetPrediction
import com.example.data.model.ModelWeightConfig
import com.example.ui.theme.AiPurple
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
fun EnsembleBreakdownCard(
    prediction: EnsembleTargetPrediction,
    weightConfig: ModelWeightConfig,
    onWeightsChanged: (ModelWeightConfig) -> Unit,
    horizonDays: Int,
    onHorizonChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var showTuneWeights by remember { mutableStateOf(false) }

    val verdictColor = when {
        prediction.trendVerdict.contains("STRONG BULLISH") -> BullishGreen
        prediction.trendVerdict.contains("BULLISH") -> BullishGreen
        prediction.trendVerdict.contains("BEARISH") -> BearishRed
        else -> NeutralGold
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("ensemble_breakdown_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = BentoSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Header Row: Horizon selector & Tune Weights toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(AiPurple.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI Ensemble",
                            tint = AiPurple,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Hybrid Ensemble Prediction",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "LSTM + XGBoost + FinBERT Fusion",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                }

                Surface(
                    onClick = { showTuneWeights = !showTuneWeights },
                    color = if (showTuneWeights) PrimaryBlue.copy(alpha = 0.18f) else BentoSurfaceVariant,
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (showTuneWeights) PrimaryBlue else BentoBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Tune",
                            tint = if (showTuneWeights) PrimaryBlue else TextSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Weights",
                            fontSize = 11.sp,
                            color = if (showTuneWeights) PrimaryBlue else TextSecondary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Horizon Picker Bar (7d, 14d, 30d)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(7, 14, 30).forEach { days ->
                    FilterChip(
                        selected = horizonDays == days,
                        onClick = { onHorizonChanged(days) },
                        label = { Text("$days-Day Target", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryBlue.copy(alpha = 0.18f),
                            selectedLabelColor = PrimaryBlue
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Target Price Banner Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BentoSurfaceVariant, RoundedCornerShape(20.dp))
                    .border(1.dp, verdictColor.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                    .padding(18.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = verdictColor.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = prediction.trendVerdict,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                color = verdictColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "Confidence: ${String.format("%.1f", prediction.confidenceScorePct)}%",
                            fontSize = 12.sp,
                            color = TextMuted,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text("TARGET PRICE", fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                            Text(
                                text = "₹${String.format("%.2f", prediction.targetPrice)}",
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            val gainSign = if (prediction.targetGainPct >= 0) "+" else ""
                            Text(
                                text = "$gainSign${String.format("%.2f", prediction.targetGainPct)}%",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = verdictColor
                            )
                            Text(
                                text = "from ₹${String.format("%.2f", prediction.currentPrice)}",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LinearProgressIndicator(
                        progress = { (prediction.confidenceScorePct / 100f).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(CircleShape),
                        color = verdictColor,
                        trackColor = BentoBorder
                    )
                }
            }

            // Expandable Tune Model Weights Section
            if (showTuneWeights) {
                Spacer(modifier = Modifier.height(16.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BentoSurfaceVariant, RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    Text(
                        text = "Customize Model Weight Blending (Formula Fusion)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SecondaryCyan
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Weight 1: LSTM
                    val lstmPct = ((weightConfig.w1Lstm / (weightConfig.w1Lstm + weightConfig.w2Xgboost)) * 100).toInt()
                    val xgPct = 100 - lstmPct

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("LSTM Time Series ($lstmPct%)", fontSize = 11.sp, color = TextSecondary)
                        Text("₹${String.format("%.2f", prediction.lstmPrice)}", fontSize = 11.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = weightConfig.w1Lstm,
                        onValueChange = { onWeightsChanged(weightConfig.copy(w1Lstm = it)) },
                        valueRange = 0.1f..0.8f,
                        colors = SliderDefaults.colors(thumbColor = PrimaryBlue, activeTrackColor = PrimaryBlue)
                    )

                    // Weight 2: XGBoost
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("XGBoost Indicators ($xgPct%)", fontSize = 11.sp, color = TextSecondary)
                        Text("₹${String.format("%.2f", prediction.xgboostPrice)}", fontSize = 11.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = weightConfig.w2Xgboost,
                        onValueChange = { onWeightsChanged(weightConfig.copy(w2Xgboost = it)) },
                        valueRange = 0.1f..0.8f,
                        colors = SliderDefaults.colors(thumbColor = SecondaryCyan, activeTrackColor = SecondaryCyan)
                    )

                    // Weight 3: FinBERT Alpha
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("FinBERT News Alpha (${String.format("%.2f", weightConfig.alphaFinbert)})", fontSize = 11.sp, color = TextSecondary)
                        Text("Score: ${String.format("%.2f", prediction.finbertScore)}", fontSize = 11.sp, color = AiPurple, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = weightConfig.alphaFinbert,
                        onValueChange = { onWeightsChanged(weightConfig.copy(alphaFinbert = it)) },
                        valueRange = 0.05f..0.30f,
                        colors = SliderDefaults.colors(thumbColor = AiPurple, activeTrackColor = AiPurple)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3 Ensemble Stream Cards
            Text("Model Stream Breakdown", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
            Spacer(modifier = Modifier.height(8.dp))

            ModelStreamItem(
                title = "1. LSTM / Transformer",
                subtitle = "Sequential Time Series Model",
                valueText = "₹${String.format("%.2f", prediction.lstmPrice)}",
                badgeText = "Path Forecast",
                icon = Icons.Default.Memory,
                iconTint = PrimaryBlue
            )

            Spacer(modifier = Modifier.height(8.dp))

            ModelStreamItem(
                title = "2. XGBoost Classifier",
                subtitle = "RSI, MACD, EMA Technicals",
                valueText = "₹${String.format("%.2f", prediction.xgboostPrice)}",
                badgeText = "Tree Signals",
                icon = Icons.Default.Psychology,
                iconTint = SecondaryCyan
            )

            Spacer(modifier = Modifier.height(8.dp))

            ModelStreamItem(
                title = "3. FinBERT Sentiment",
                subtitle = "Scraped Dalal Street News Feeds",
                valueText = String.format("%.2f", prediction.finbertScore),
                badgeText = if (prediction.finbertScore >= 0) "Positive Alpha" else "Negative Alpha",
                icon = Icons.Default.Newspaper,
                iconTint = AiPurple
            )
        }
    }
}

private val ModelWeightConfig.w1LstmPct: Int
    get() = ((w1Lstm / (w1Lstm + w2Xgboost)) * 100).toInt()

private val ModelWeightConfig.w2XgPct: Int
    get() = (100 - w1LstmPct)

@Composable
private fun ModelStreamItem(
    title: String,
    subtitle: String,
    valueText: String,
    badgeText: String,
    icon: ImageVector,
    iconTint: Color
) {
    Surface(
        color = BentoSurfaceVariant,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(iconTint.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(subtitle, fontSize = 11.sp, color = TextMuted)
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(valueText, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Surface(
                    color = iconTint.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = badgeText,
                        fontSize = 9.sp,
                        color = iconTint,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}
