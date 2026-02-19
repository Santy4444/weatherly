# Weatherly

Aplicação web para consultar o tempo em tempo real.

![Java](https://img.shields.io/badge/Java-17+-ED8B00?logo=openjdk)
![Maven](https://img.shields.io/badge/Maven-3.x-C71A36?logo=apachemaven)
![License](https://img.shields.io/badge/license-MIT-blue)

## Funcionalidades

- Consulta o tempo atual por cidade (Lisboa, Porto, Londres, etc.)
- Temperatura, sensação térmica, humidade, pressão, vento e nuvens
- Modo claro e escuro com animação de transição
- Dados por (OpenWeatherMap API)

## Pré-requisitos

- [Java 17](https://adoptium.net/) ou superior
- [Maven](https://maven.apache.org/)
- Chave de API gratuita da [OpenWeatherMap](https://openweathermap.org/api)

## Instalação

1. **Clone o repositório**
   ```bash
   git clone https://github.com/Santy4444/weatherly.git
   cd weatherly
   ```

2. **Obtenha uma API Key**
   - Registe-se em [OpenWeatherMap](https://openweathermap.org/api)
   - Copie a API Key em [API keys](https://home.openweathermap.org/api_keys)

3. **Configure a variável de ambiente**

   **Windows (PowerShell):**
   ```powershell
   $env:OPENWEATHER_API_KEY = "sua_api_key_aqui"
   ```

   **Linux / macOS:**
   ```bash
   export OPENWEATHER_API_KEY="sua_api_key_aqui"
   ```

## Executar

```bash
mvn compile exec:java
```

Abra [http://localhost:8080](http://localhost:8080) no browser.

## API utilizada

- [Current Weather Data](https://openweathermap.org/current) da OpenWeatherMap
- Endpoint: `/api/weather?city=NomeDaCidade`
- Dados em Celsius

## Licença

MIT
