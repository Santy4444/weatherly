package pt.weather;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
<<<<<<< HEAD
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
=======
>>>>>>> de1c97ecbec0df1ce0a36751c462a194e4a7c5fc

public class WeatherServer {

    private static final String OPENWEATHER_API_KEY = System.getenv("OPENWEATHER_API_KEY");
    private static final String OPENWEATHER_URL = "https://api.openweathermap.org/data/2.5/weather";
    private static final int PORT = 8080;
    private static final String STATIC_DIR = "src/main/resources/static";

    public static void main(String[] args) throws IOException {
        if (OPENWEATHER_API_KEY == null || OPENWEATHER_API_KEY.isBlank()) {
            System.err.println("ERRO: Defina a variável de ambiente OPENWEATHER_API_KEY com a sua chave da OpenWeatherMap.");
            System.err.println("Obtenha uma chave em: https://openweathermap.org/api");
            System.exit(1);
        }

        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/", new StaticFileHandler());
<<<<<<< HEAD
        server.createContext("/api/weather/icon", new WeatherIconHandler());
=======
>>>>>>> de1c97ecbec0df1ce0a36751c462a194e4a7c5fc
        server.createContext("/api/weather", new WeatherApiHandler());

        server.setExecutor(null);
        server.start();
<<<<<<< HEAD
        System.out.println("O server esta a correr em http://localhost:" + PORT);
=======
        System.out.println("Servidor a correr em http://localhost:" + PORT);
        System.out.println("Abra o browser em http://localhost:" + PORT);
>>>>>>> de1c97ecbec0df1ce0a36751c462a194e4a7c5fc
    }

    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "text/plain", "Method Not Allowed");
                return;
            }
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/")) path = "/index.html";
            Path basePath = Paths.get(STATIC_DIR).toAbsolutePath().normalize();
            String reqPath = path.substring(1).replace("\\", "/");
            Path file = basePath.resolve(reqPath).normalize();
            if (!file.startsWith(basePath)) {
                sendResponse(exchange, 403, "text/plain", "Forbidden");
                return;
            }
            if (!Files.exists(file) || !Files.isRegularFile(file)) {
                if (path.startsWith("/api")) {
                    exchange.close();
                    return;
                }
                sendResponse(exchange, 404, "text/plain", "Not Found");
                return;
            }
            String contentType = getContentType(path);
            byte[] bytes = Files.readAllBytes(file);
            exchange.getResponseHeaders().set("Content-Type", contentType);
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.sendResponseHeaders(200, bytes.length);
            exchange.getResponseBody().write(bytes);
            exchange.close();
        }

        private String getContentType(String path) {
            if (path.endsWith(".html")) return "text/html; charset=UTF-8";
            if (path.endsWith(".css")) return "text/css; charset=UTF-8";
            if (path.endsWith(".js")) return "application/javascript; charset=UTF-8";
            if (path.endsWith(".ico")) return "image/x-icon";
            return "application/octet-stream";
        }
    }

    static class WeatherApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "application/json", "{\"error\":\"Method Not Allowed\"}");
                return;
            }
            URI uri = exchange.getRequestURI();
            String query = uri.getQuery();
            String city = null;
            if (query != null) {
                for (String param : query.split("&")) {
                    if (param.startsWith("city=")) {
                        city = param.substring(5).trim();
                        try {
                            city = java.net.URLDecoder.decode(city, StandardCharsets.UTF_8);
                        } catch (Exception ignored) {}
                        break;
                    }
                }
            }
            if (city == null || city.isEmpty()) {
                sendResponse(exchange, 400, "application/json",
                    "{\"cod\":400,\"message\":\"Parâmetro 'city' é obrigatório\"}");
                return;
            }

            String url = OPENWEATHER_URL + "?q=" + URLEncoder.encode(city, StandardCharsets.UTF_8)
                + "&appid=" + OPENWEATHER_API_KEY
                + "&units=metric"
                + "&lang=pt";

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
                String body = response.body();
                int status = response.statusCode();
                if (status == 200) {
                    exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                    exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                    exchange.sendResponseHeaders(200, body.getBytes(StandardCharsets.UTF_8).length);
                    exchange.getResponseBody().write(body.getBytes(StandardCharsets.UTF_8));
                } else {
                    sendResponse(exchange, status, "application/json", body);
                }
            } catch (Exception e) {
                sendResponse(exchange, 502, "application/json",
                    "{\"cod\":502,\"message\":\"Erro ao contactar a API: " + e.getMessage() + "\"}");
            }
            exchange.close();
        }
    }

<<<<<<< HEAD
    static class WeatherIconHandler implements HttpHandler {
        private static final String ICON_BASE = "https://openweathermap.org/img/wn/";
        private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";
        private static final Map<String, byte[]> CACHE = new ConcurrentHashMap<>();
        private static final HttpClient ICON_CLIENT = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(4))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "text/plain", "Method Not Allowed");
                return;
            }
            String code = null;
            String query = exchange.getRequestURI().getQuery();
            if (query != null) {
                for (String param : query.split("&")) {
                    if (param.startsWith("code=")) {
                        code = param.substring(5).trim();
                        try {
                            code = java.net.URLDecoder.decode(code, StandardCharsets.UTF_8);
                        } catch (Exception ignored) {}
                        break;
                    }
                }
            }
            if (code == null || code.isEmpty() || !code.matches("^[0-9][0-9][dn]$")) {
                sendResponse(exchange, 400, "application/json", "{\"error\":\"Parâmetro 'code' inválido\"}");
                return;
            }
            byte[] body = CACHE.get(code);
            if (body == null) {
                String iconUrl = ICON_BASE + code + "@2x.png";
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(iconUrl))
                        .header("User-Agent", USER_AGENT)
                        .GET()
                        .build();
                try {
                    HttpResponse<byte[]> response = ICON_CLIENT.send(request, HttpResponse.BodyHandlers.ofByteArray());
                    if (response.statusCode() != 200) {
                        sendResponse(exchange, 502, "application/json", "{\"error\":\"Ícone não disponível\"}");
                        return;
                    }
                    body = response.body();
                    if (body != null && body.length > 0) {
                        CACHE.put(code, body);
                    }
                } catch (Exception e) {
                    sendResponse(exchange, 502, "application/json",
                            "{\"error\":\"Erro ao obter ícone: " + e.getMessage().replace("\"", "'") + "\"}");
                    exchange.close();
                    return;
                }
            }
            if (body == null || body.length == 0) {
                sendResponse(exchange, 502, "application/json", "{\"error\":\"Ícone vazio\"}");
                return;
            }
            exchange.getResponseHeaders().set("Content-Type", "image/png");
            exchange.getResponseHeaders().set("Cache-Control", "public, max-age=86400");
            exchange.sendResponseHeaders(200, body.length);
            exchange.getResponseBody().write(body);
            exchange.close();
        }
    }

=======
>>>>>>> de1c97ecbec0df1ce0a36751c462a194e4a7c5fc
    private static void sendResponse(HttpExchange exchange, int code, String contentType, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(code, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.close();
    }
}
