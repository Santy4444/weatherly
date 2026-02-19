(function () {
    'use strict';

    const form = document.getElementById('search-form');
    const cityInput = document.getElementById('city-input');
    const searchBtn = document.getElementById('search-btn');
    const loading = document.getElementById('loading');
    const errorCard = document.getElementById('error');
    const errorMessage = document.getElementById('error-message');
    const weatherCard = document.getElementById('weather-card');

    const elements = {
        cityName: document.getElementById('city-name'),
        country: document.getElementById('country'),
        weatherIcon: document.getElementById('weather-icon'),
        temp: document.getElementById('temp'),
        feelsLike: document.getElementById('feels-like'),
        description: document.getElementById('description'),
        humidity: document.getElementById('humidity'),
        pressure: document.getElementById('pressure'),
        wind: document.getElementById('wind'),
        clouds: document.getElementById('clouds')
    };

    function showLoading(show) {
        loading.classList.toggle('hidden', !show);
        if (show) {
            errorCard.classList.add('hidden');
            weatherCard.classList.add('hidden');
        }
    }

    function showError(message) {
        errorMessage.textContent = message;
        errorCard.classList.remove('hidden');
        weatherCard.classList.add('hidden');
        loading.classList.add('hidden');
    }

    function showWeather(data) {
        errorCard.classList.add('hidden');
        loading.classList.add('hidden');

        const w = data.weather && data.weather[0];
        const m = data.main || {};
        const wnd = data.wind || {};
        const cld = data.clouds || {};

        elements.cityName.textContent = data.name || '—';
        elements.country.textContent = data.sys && data.sys.country ? data.sys.country : '';

        const icon = w && w.icon ? w.icon : '01d';
        elements.weatherIcon.src = '/api/weather/icon?code=' + encodeURIComponent(icon);
        elements.weatherIcon.alt = w ? w.description : '';

        const temp = m.temp != null ? Math.round(m.temp) : '—';
        elements.temp.textContent = temp + ' °C';

        const feels = m.feels_like != null ? Math.round(m.feels_like) : '—';
        elements.feelsLike.textContent = 'Sensação térmica: ' + feels + ' °C';

        elements.description.textContent = w && w.description ? w.description : '—';

        elements.humidity.textContent = (m.humidity != null ? m.humidity + ' %' : '—');
        elements.pressure.textContent = (m.pressure != null ? m.pressure + ' hPa' : '—');
        elements.wind.textContent = (wnd.speed != null ? wnd.speed + ' m/s' : '—');
        elements.clouds.textContent = (cld.all != null ? cld.all + ' %' : '—');

        weatherCard.classList.remove('hidden');
    }

    async function fetchWeather(city) {
        const encoded = encodeURIComponent(city.trim());
        const url = '/api/weather?city=' + encoded;
        const res = await fetch(url);
        const data = await res.json().catch(function () {
            return { cod: res.status, message: 'Resposta inválida do servidor.' };
        });

        if (!res.ok) {
            const msg = data.message || 'Erro ao obter o tempo. Tente outra cidade.';
            showError(msg);
            return;
        }

        if (data.cod && data.cod !== 200) {
            showError(data.message || 'Cidade não encontrada.');
            return;
        }

        showWeather(data);
    }

    form.addEventListener('submit', function (e) {
        e.preventDefault();
        const city = cityInput.value.trim();
        if (!city) return;

        searchBtn.disabled = true;
        showLoading(true);
        fetchWeather(city).finally(function () {
            searchBtn.disabled = false;
        });
    });
})();
