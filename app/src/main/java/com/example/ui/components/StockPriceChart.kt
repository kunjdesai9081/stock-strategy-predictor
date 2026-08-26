package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Candlestick
import com.example.data.model.TechnicalIndicators
import kotlin.math.abs
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun StockPriceChart(
    candles: List<Candlestick>,
    technicals: TechnicalIndicators,
    modifier: Modifier = Modifier
) {
    var isCandlestickMode by remember { mutableStateOf(true) }
    var subChartType by remember { mutableStateOf("MACD") } // MACD, RSI, None
    var selectedIndex by remember { mutableStateOf<Int?>(null) }

    val dateFormat = remember { SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()) }

    val selectedCandle = selectedIndex?.let { idx ->
        if (idx in candles.indices) candles[idx] else null
    } ?: candles.lastOrNull()

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("stock_price_chart_container"),
        shape = RoundedCornerShape(24.dp),
        color = BentoSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder),
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Top Toolbar: Chart type & Subchart toggles
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    FilterChip(
                        selected = isCandlestickMode,
                        onClick = { isCandlestickMode = true },
                        label = { Text("Candles", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                        leadingIcon = { Icon(Icons.Default.BarChart, contentDescription = null, modifier = Modifier.size(14.dp)) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryBlue.copy(alpha = 0.18f),
                            selectedLabelColor = PrimaryBlue
                        ),
                        modifier = Modifier.testTag("chip_candles")
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    FilterChip(
                        selected = !isCandlestickMode,
                        onClick = { isCandlestickMode = false },
                        label = { Text("Line", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                        leadingIcon = { Icon(Icons.Default.ShowChart, contentDescription = null, modifier = Modifier.size(14.dp)) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryBlue.copy(alpha = 0.18f),
                            selectedLabelColor = PrimaryBlue
                        ),
                        modifier = Modifier.testTag("chip_line")
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    FilterChip(
                        selected = subChartType == "MACD",
                        onClick = { subChartType = if (subChartType == "MACD") "NONE" else "MACD" },
                        label = { Text("MACD", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SecondaryCyan.copy(alpha = 0.18f),
                            selectedLabelColor = SecondaryCyan
                        )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    FilterChip(
                        selected = subChartType == "RSI",
                        onClick = { subChartType = if (subChartType == "RSI") "NONE" else "RSI" },
                        label = { Text("RSI", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeutralGold.copy(alpha = 0.18f),
                            selectedLabelColor = NeutralGold
                        )
                    )
                }
            }

            // Active Crosshair Value Bar
            selectedCandle?.let { c ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = dateFormat.format(Date(c.timestamp)),
                        fontSize = 11.sp,
                        color = TextMuted,
                        fontWeight = FontWeight.Bold
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("O: ₹${String.format("%.1f", c.open)}", fontSize = 11.sp, color = TextSecondary)
                        Text("H: ₹${String.format("%.1f", c.high)}", fontSize = 11.sp, color = BullishGreen, fontWeight = FontWeight.Bold)
                        Text("L: ₹${String.format("%.1f", c.low)}", fontSize = 11.sp, color = BearishRed, fontWeight = FontWeight.Bold)
                        Text("C: ₹${String.format("%.1f", c.close)}", fontSize = 11.sp, color = TextPrimary, fontWeight = FontWeight.Black)
                    }
                }
            }

        // Main Chart Canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .pointerInput(candles) {
                        detectTapGestures { offset ->
                            val width = size.width
                            val count = candles.size
                            if (count > 0 && width > 0) {
                                val candleWidth = width / count
                                val idx = (offset.x / candleWidth).toInt().coerceIn(0, count - 1)
                                selectedIndex = idx
                            }
                        }
                    }
            ) {
                if (candles.isEmpty()) return@Canvas

                val minPrice = candles.minOf { it.low } * 0.995f
                val maxPrice = candles.maxOf { it.high } * 1.005f
                val priceRange = (maxPrice - minPrice).coerceAtLeast(0.1f)

                val maxVol = candles.maxOf { it.volume }.coerceAtLeast(1L).toFloat()

                val chartWidth = size.width
                val chartHeight = size.height
                val count = candles.size
                val itemWidth = chartWidth / count

                // 1. Gridlines
                val gridLines = 4
                for (i in 1..gridLines) {
                    val y = (chartHeight / gridLines) * i
                    drawLine(
                        color = BentoBorder.copy(alpha = 0.5f),
                        start = Offset(0f, y),
                        end = Offset(chartWidth, y),
                        strokeWidth = 1.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    )
                }

                // 2. Volume Bars (Bottom 25%)
                for (i in candles.indices) {
                    val c = candles[i]
                    val x = (i * itemWidth) + (itemWidth / 2f)
                    val volHeight = (c.volume / maxVol) * (chartHeight * 0.25f)
                    val isUp = c.close >= c.open
                    val color = if (isUp) BullishGreen.copy(alpha = 0.25f) else BearishRed.copy(alpha = 0.25f)

                    drawRect(
                        color = color,
                        topLeft = Offset(x - (itemWidth * 0.35f), chartHeight - volHeight),
                        size = Size(itemWidth * 0.7f, volHeight)
                    )
                }

                // 3. Main Candlestick / Line Drawing
                val linePath = Path()

                for (i in candles.indices) {
                    val c = candles[i]
                    val x = (i * itemWidth) + (itemWidth / 2f)

                    val openY = chartHeight - (((c.open - minPrice) / priceRange) * chartHeight)
                    val closeY = chartHeight - (((c.close - minPrice) / priceRange) * chartHeight)
                    val highY = chartHeight - (((c.high - minPrice) / priceRange) * chartHeight)
                    val lowY = chartHeight - (((c.low - minPrice) / priceRange) * chartHeight)

                    val isUp = c.close >= c.open
                    val color = if (isUp) BullishGreen else BearishRed

                    if (isCandlestickMode) {
                        // High/Low Wick
                        drawLine(
                            color = color,
                            start = Offset(x, highY),
                            end = Offset(x, lowY),
                            strokeWidth = 1.5.dp.toPx()
                        )

                        // Candle Body
                        val bodyTop = minOf(openY, closeY)
                        val bodyHeight = abs(openY - closeY).coerceAtLeast(2f)

                        drawRect(
                            color = color,
                            topLeft = Offset(x - (itemWidth * 0.35f), bodyTop),
                            size = Size(itemWidth * 0.7f, bodyHeight)
                        )
                    } else {
                        // Line chart mode
                        if (i == 0) {
                            linePath.moveTo(x, closeY)
                        } else {
                            linePath.lineTo(x, closeY)
                        }
                    }
                }

                if (!isCandlestickMode) {
                    drawPath(
                        path = linePath,
                        color = PrimaryBlue,
                        style = Stroke(width = 2.5.dp.toPx())
                    )
                }

                // 4. Selected Index Crosshair
                selectedIndex?.let { idx ->
                    if (idx in candles.indices) {
                        val c = candles[idx]
                        val x = (idx * itemWidth) + (itemWidth / 2f)
                        val closeY = chartHeight - (((c.close - minPrice) / priceRange) * chartHeight)

                        drawLine(
                            color = PrimaryBlue,
                            start = Offset(x, 0f),
                            end = Offset(x, chartHeight),
                            strokeWidth = 1.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
                        )

                        drawCircle(
                            color = PrimaryBlue,
                            radius = 6.dp.toPx(),
                            center = Offset(x, closeY)
                        )
                    }
                }
            }
        }

        // Sub-chart (MACD / RSI)
        if (subChartType != "NONE") {
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                color = BentoSurfaceVariant,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = if (subChartType == "MACD") "MACD Histogram (${String.format("%.2f", technicals.macdHistogram)})"
                               else "RSI Gauge (${String.format("%.1f", technicals.rsi)})",
                        fontSize = 11.sp,
                        color = if (subChartType == "MACD") SecondaryCyan else NeutralGold,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        val width = size.width
                        val height = size.height

                        if (subChartType == "RSI") {
                            // RSI 30/70 Reference lines
                            val y30 = height - (30f / 100f * height)
                            val y70 = height - (70f / 100f * height)

                            drawLine(BentoBorder, Offset(0f, y70), Offset(width, y70), pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f)))
                            drawLine(BentoBorder, Offset(0f, y30), Offset(width, y30), pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f)))

                            val rsiY = height - ((technicals.rsi / 100f) * height)
                            val barColor = when {
                                technicals.rsi > 70f -> BearishRed
                                technicals.rsi < 30f -> BullishGreen
                                else -> NeutralGold
                            }

                            drawRect(
                                color = barColor.copy(alpha = 0.3f),
                                topLeft = Offset(0f, rsiY),
                                size = Size(width, height - rsiY)
                            )
                        } else if (subChartType == "MACD") {
                            val midY = height / 2f
                            drawLine(BentoBorder, Offset(0f, midY), Offset(width, midY))

                            val histColor = if (technicals.macdHistogram >= 0) BullishGreen else BearishRed
                            val barH = (technicals.macdHistogram * 10f).coerceIn(-height / 2f, height / 2f)

                            if (barH >= 0) {
                                drawRect(
                                    color = histColor,
                                    topLeft = Offset(width * 0.2f, midY - barH),
                                    size = Size(width * 0.6f, barH)
                                )
                            } else {
                                drawRect(
                                    color = histColor,
                                    topLeft = Offset(width * 0.2f, midY),
                                    size = Size(width * 0.6f, abs(barH))
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
}
