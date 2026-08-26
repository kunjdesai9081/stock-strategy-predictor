package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DeveloperMode
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AiPurple
import com.example.ui.theme.BentoBorder
import com.example.ui.theme.BentoSurface
import com.example.ui.theme.BentoSurfaceVariant
import com.example.ui.theme.NeutralGold
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SecondaryCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun SystemDesignScreen(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section 1: Architecture Blueprint Overview
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("system_architecture_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = BentoSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(PrimaryBlue.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Hub, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Real-Time AI System Architecture", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("SYSTEM_DESIGN.md • Hybrid Ensemble Strategy", fontSize = 11.sp, color = TextMuted)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Surface(
                        color = BentoSurfaceVariant,
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = "[ WebSocket / Data Stream ] ---> [ Feature Engineering ]\n" +
                                   "                               (RSI, MACD, EMA, VWAP)\n" +
                                   "                                         |\n" +
                                   "  ┌──────────────────────────────────────┴──────────────────────────────────────┐\n" +
                                   "  │                        Hybrid AI Model Pipeline                            │\n" +
                                   "  ├──────────────────────────┬──────────────────────────┬──────────────────────┤\n" +
                                   "  │    LSTM / Transformer    │     XGBoost Classifier   │       FinBERT        │\n" +
                                   "  │  (Price Sequence Model)  │  (Trend Signal Engine)   │   (News Sentiment)   │\n" +
                                   "  └────────────┬─────────────┴────────────┬─────────────┴──────────┬───────────┘\n" +
                                   "               │                          │                        │\n" +
                                   "               └──────────────────┬───────┴────────────────────────┘\n" +
                                   "                                  v\n" +
                                   "                  [ Meta-Ensemble / Stacking Layer ]\n" +
                                   "                                  │\n" +
                                   "                                  v\n" +
                                   "                   ┌──────────────────────────────┐\n" +
                                   "                   │        Output Engine         │\n" +
                                   "                   ├──────────────────────────────┤\n" +
                                   "                   │ - Predicted Target Price T   │\n" +
                                   "                   │ - Trend: BULLISH / BEARISH   │\n" +
                                   "                   │ - Confidence Score (%)       │\n" +
                                   "                   └──────────────────────────────┘",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            color = SecondaryCyan,
                            lineHeight = 14.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
        }

        // Section 2: Mathematical Equations & Target Formulation
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("system_formulas_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = BentoSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(SecondaryCyan.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Functions, contentDescription = null, tint = SecondaryCyan, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Ensemble Equations & Formulas", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("Target Price T_{t+k} & Technical Indicator Math", fontSize = 11.sp, color = TextMuted)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Surface(
                        color = BentoSurfaceVariant,
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SecondaryCyan.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Target Price Fusion Model:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeutralGold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "T_{t+k} = w_1 · ŷ_LSTM + w_2 · ŷ_XGBoost + α · Sentiment_Score",
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Monospace,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Where:\n" +
                                       "• ŷ_LSTM: Time-series forecasted price trajectory.\n" +
                                       "• ŷ_XGBoost: Decision tree technical price target.\n" +
                                       "• α: Multiplier bounded by FinBERT news sentiment [-1, 1].\n" +
                                       "• w_1, w_2: Model weightings where w_1 + w_2 = 1.0.",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }

        // Section 3: Python Backend Reference Implementation
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("python_reference_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = BentoSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(AiPurple.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Code, contentDescription = null, tint = AiPurple, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Python Reference Script", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("FastAPI / Technical Indicator Engine", fontSize = 11.sp, color = TextMuted)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Surface(
                        color = BentoSurfaceVariant,
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = "def generate_stock_features(df: pd.DataFrame):\n" +
                                   "    # Moving Averages\n" +
                                   "    df['SMA_20'] = df['Close'].rolling(20).mean()\n" +
                                   "    df['EMA_12'] = df['Close'].ewm(span=12).mean()\n" +
                                   "    df['EMA_26'] = df['Close'].ewm(span=26).mean()\n\n" +
                                   "    # RSI Calculation (14-period)\n" +
                                   "    delta = df['Close'].diff()\n" +
                                   "    gain = delta.where(delta > 0, 0).rolling(14).mean()\n" +
                                   "    loss = (-delta.where(delta < 0, 0)).rolling(14).mean()\n" +
                                   "    df['RSI'] = 100 - (100 / (1 + (gain / loss)))\n\n" +
                                   "    # MACD Calculation\n" +
                                   "    df['MACD'] = df['EMA_12'] - df['EMA_26']\n" +
                                   "    df['MACD_Signal'] = df['MACD'].ewm(span=9).mean()\n" +
                                   "    return df.dropna()",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = TextSecondary,
                            lineHeight = 15.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
        }
    }
}
