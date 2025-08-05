// Replace 'YOUR_API_KEY' with your actual Alpha Vantage API key
const apiKey = '6S6G04WAEILWEHTT'; // Your API key here

document.getElementById('activate-strategy').addEventListener('click', function() {
    // Get the selected stock symbol from the dropdown
    const symbol = document.getElementById('symbol').value;
    const type = document.getElementById('type').value;
    const riskPercentage = parseFloat(document.getElementById('risk-percentage').value) || 0;

    // Fetch data from Alpha Vantage API
    fetch(`https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol=${symbol}&interval=5min&apikey=${apiKey}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok ' + response.statusText);
            }
            return response.json();
        })
        .then(data => {
            console.log('Data fetched:', data);
            // Here you can perform calculations or update the UI with the fetched data
            const timeSeries = data['Time Series (5min)'];
            const latestTime = Object.keys(timeSeries)[0];
            const latestData = timeSeries[latestTime];
            const latestPrice = parseFloat(latestData['1. open']); // You can change this to '4. close' or other fields as needed

            // Calculate risk based on the latest price
            const riskAmount = (riskPercentage / 100) * latestPrice;

            // Display the latest price and risk amount
            const output = `
                <h4>Latest price for ${symbol} at ${latestTime}: $${latestPrice.toFixed(2)}</h4>
                <h4>Risk Amount (based on ${riskPercentage}%): $${riskAmount.toFixed(2)}</h4>
            `;
            document.getElementById('strategy-output').innerHTML = output;

            // Prepare data for chart
            const labels = Object.keys(timeSeries).slice(0, 10); // Get the last 10 time points
            const prices = labels.map(time => parseFloat(timeSeries[time]['1. open']));

            // Create chart
            const ctx = document.getElementById('stockChart').getContext('2d');
            const stockChart = new Chart(ctx, {
                type: 'line',
                data: {
                    labels: labels,
                    datasets: [{
                        label: `${symbol} Price`,
                        data: prices,
                        borderColor: 'rgba(75, 192, 192, 1)',
                        borderWidth: 2,
                        fill: false
                    }]
                },
                options: {
                    responsive: true,
                    scales: {
                        x: {
                            title: {
                                display: true,
                                text: 'Time'
                            }
                        },
                        y: {
                            title: {
                                display: true,
                                text: 'Price ($)'
                            }
                        }
                    }
                }
            });
        })
        .catch(error => {
            console.error('There was a problem with the fetch operation:', error);
        });
});
