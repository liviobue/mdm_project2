function predictWaterTemp() {
    const siteId = document.getElementById('site-id').value;
    const unitId = document.getElementById('unit-id').value;
    const readDate = document.getElementById('read-date').value;
    const salinity = parseFloat(document.getElementById('salinity').value);
    const dissolvedOxygen = parseFloat(document.getElementById('dissolved-oxygen').value);
    const ph = parseFloat(document.getElementById('ph').value);
    const secchiDepth = parseFloat(document.getElementById('secchi-depth').value);
    const waterDepth = parseFloat(document.getElementById('water-depth').value);
    const waterTemp = parseFloat(document.getElementById('water-temp').value);
    const airTempCelsius = parseFloat(document.getElementById('air-temp-celsius').value);
    const airTempFahrenheit = parseFloat(document.getElementById('air-temp-fahrenheit').value);
    const time = document.getElementById('time').value;
    const fieldTech = document.getElementById('field-tech').value;
    const dateVerified = document.getElementById('date-verified').value;
    const whoVerified = document.getElementById('who-verified').value;
    const airTempC = parseFloat(document.getElementById('air-temp-c').value);
    const year = parseInt(document.getElementById('year').value);
  
    // Call the predictWaterTemp function from the TimeSeriesModel class in your Spring Boot application
    fetch('/predict', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        siteId,
        unitId,
        readDate,
        salinity,
        dissolvedOxygen,
        ph,
        secchiDepth,
        waterDepth,
        waterTemp,
        airTempCelsius,
        airTempFahrenheit,
        time,
        fieldTech,
        dateVerified,
        whoVerified,
        airTempC,
        year
      })
    })
    .then(response => response.json())
    .then(data => {
      const predictionResult = document.getElementById('prediction-result');
      predictionResult.textContent = `Predicted Water Temperature: ${data.predictedWaterTemp} °C`;
    })
    .catch(error => {
      console.error('Error:', error);
      const predictionResult = document.getElementById('prediction-result');
      predictionResult.textContent = 'Error occurred while making the prediction.';
    });
  }
  