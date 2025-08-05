// Replace 'YOUR_API_KEY' with your actual Alpha Vantage API key
const apiKey = '6S6G04WAEILWEHTT'; // Your API key here

document.getElementById('activate-strategy').addEventListener('click', function() {
    // Get the selected stock symbol from the dropdown
    const symbol = document.getElementById('symbol').value;

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
            const latestPrice = latestData['1. open']; // You can change this to '4. close' or other fields as needed

            // Display the latest price in the console or update the UI
            console.log(`Latest price for ${symbol} at ${latestTime}: $${latestPrice}`);
        })
        .catch(error => {
            console.error('There was a problem with the fetch operation:', error);
        });
});
