# Real-Time AI Stock Price & Target Prediction System

This document provides the complete architecture design, algorithmic formulas, technical indicators, and implementation strategy for building a real-time AI-based stock target and trend prediction platform.

---

## 1. System Architecture Overview

```
[ WebSocket / Data Stream ] ---> [ Feature Engineering Engine ]
(YFinance / Alpaca / Polygon)    (RSI, MACD, SMA, EMA, VWAP)
                                             |
                                             v
┌────────────────────────────────────────────────────────────────────────┐
│                        Hybrid AI Model Pipeline                        │
├──────────────────────────┬──────────────────────────┬──────────────────┤
│    LSTM / Transformer    │     XGBoost Classifier   │     FinBERT      │
│  (Price Sequence Model)  │  (Trend Signal Engine)   │ (News Sentiment) │
└────────────┬─────────────┴────────────┬─────────────┴────────┬─────────┘
             │                          │                      │
             └──────────────────┬───────┴──────────────────────┘
                                v
                [ Meta-Ensemble / Stacking Layer ]
                                │
                                v
                 ┌───────────────────────────────┐
                 │        Output Engine          │
                 ├───────────────────────────────┤
                 │ - Predicted Target Price      │
                 │ - Trend: BULLISH / BEARISH    │
                 │ - Confidence Score (%)        │
                 └───────────────────────────────┘
```

---

## 2. Recommended ML Models

| Task | Recommended Algorithm | Function |
| :--- | :--- | :--- |
| **Price Target Forecasting** | LSTM / Transformer | Models dynamic sequential price dependencies over past $N$ time steps. |
| **Trend Classification** | XGBoost / LightGBM | Predicts direction (Bullish/Bearish/Neutral) using structured technical indicators. |
| **Market Sentiment Analysis** | FinBERT | Scrapes news feeds to adjust targets during macro volatility. |
| **Final Output Fusion** | Ridge Regression / Blending | Combines model predictions with dynamic weightings based on recent model performance. |

---

## 3. Mathematical Formulas & Technical Indicators

### 3.1 Technical Indicator Calculations

#### Relative Strength Index (RSI)
RSI measures momentum on a 0 to 100 scale:
$$\text{RS} = \frac{\text{Average Gain over } N \text{ periods}}{\text{Average Loss over } N \text{ periods}}$$
$$\text{RSI} = 100 - \left( \frac{100}{1 + \text{RS}} \right)$$

#### Exponential Moving Average (EMA)
Giving higher weight to recent prices:
$$\text{EMA}_t = \left( P_t \times \left( \frac{2}{N + 1} \right) \right) + \left( \text{EMA}_{t-1} \times \left(1 - \frac{2}{N + 1}\right) \right)$$

#### Moving Average Convergence Divergence (MACD)
$$\text{MACD Line} = \text{EMA}_{12}(P) - \text{EMA}_{26}(P)$$
$$\text{Signal Line} = \text{EMA}_9(\text{MACD Line})$$
$$\text{Histogram} = \text{MACD Line} - \text{Signal Line}$$

### 3.2 Target Price Estimation Model
The target price $T_{t+k}$ for $k$ steps ahead is derived using a weighted ensemble:

$$T_{t+k} = w_1 \cdot \hat{y}_{\text{LSTM}} + w_2 \cdot \hat{y}_{\text{XGBoost}} + \alpha \cdot \text{Sentiment\_Score}$$

Where:
* $\hat{y}_{\text{LSTM}}$ is the time-series forecasted price.
* $\hat{y}_{\text{XGBoost}}$ is the decision tree price prediction.
* $\alpha$ is a scaling multiplier bound by normalized sentiment scores $[-1, 1]$.
* $w_1, w_2$ are model weights (where $w_1 + w_2 = 1$).

---

## 4. Python Implementation Reference

```python
import numpy as np
import pandas as pd
from sklearn.ensemble import HistGradientBoostingRegressor
import ta # Technical Analysis Library

def generate_stock_features(df: pd.DataFrame) -> pd.DataFrame:
    """Calculates real-time technical indicators for trend prediction."""
    df = df.copy()
    
    # Simple & Exponential Moving Averages
    df['SMA_20'] = df['Close'].rolling(window=20).mean()
    df['EMA_12'] = df['Close'].ewm(span=12, adjust=False).mean()
    df['EMA_26'] = df['Close'].ewm(span=26, adjust=False).mean()
    
    # RSI (14-period)
    delta = df['Close'].diff()
    gain = (delta.where(delta > 0, 0)).rolling(window=14).mean()
    loss = (-delta.where(delta < 0, 0)).rolling(window=14).mean()
    rs = gain / loss
    df['RSI'] = 100 - (100 / (1 + rs))
    
    # MACD
    df['MACD'] = df['EMA_12'] - df['EMA_26']
    df['MACD_Signal'] = df['MACD'].ewm(span=9, adjust=False).mean()
    
    # Price Trend Signal (-1: Bearish, 0: Neutral, 1: Bullish)
    df['Trend_Signal'] = np.where((df['RSI'] < 30) & (df['MACD'] > df['MACD_Signal']), 1,
                         np.where((df['RSI'] > 70) & (df['MACD'] < df['MACD_Signal']), -1, 0))
    
    return df.dropna()

def calculate_target_price(current_price: float, predicted_return_pct: float) -> float:
    """Calculates projected stock target price."""
    target_price = current_price * (1 + (predicted_return_pct / 100))
    return round(target_price, 2)
```

---

## 5. Technology Stack Summary

* **Frontend:** Next.js / React with Lightweight Charts (TradingView) or Jetpack Compose native canvas rendering.
* **Backend API:** FastAPI (Python) / Coroutine Stream Service for real-time WebSockets & AI predictions.
* **ML Frameworks:** PyTorch (LSTM/Transformers), XGBoost / LightGBM, HuggingFace (FinBERT).
* **Data Sources:** Alpaca API / Polygon.io / Yahoo Finance via WebSockets.
* **Database:** Room DB (Android local caching) + Redis / TimescaleDB.
