package app.service;

import app.dto.CreditsDTO;
import app.dto.GenreApiResultDTO;
import app.dto.MovieApiResultDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.Properties;

public class MovieFetcher {
    private static final String API_KEY = loadApiKey();
    private static final String BASE_URL = "https://api.themoviedb.org/3";

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private static String loadApiKey() {
        Properties properties = new Properties();
        try (InputStream input = MovieFetcher.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return null;
            }
            properties.load(input);
            return properties.getProperty("TMDB_API_KEY");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public MovieApiResultDTO fetchMovies(int page) throws IOException, InterruptedException {
        String url = String.format("%s/discover/movie?api_key=%s&with_origin_country=DK&language=da-DK&release_date.gte=%s&release_date.lte=%s&page=%d",
                BASE_URL, API_KEY, LocalDate.now().minusYears(5).toString(), LocalDate.now().toString(), page);
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IOException("Failed to fetch movies. Status code: " + response.statusCode());
        }
        return objectMapper.readValue(response.body(), MovieApiResultDTO.class);
    }

    public CreditsDTO fetchCredits(int movieId) throws IOException, InterruptedException {
        String url = String.format("%s/movie/%d/credits?api_key=%s", BASE_URL, movieId, API_KEY);
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IOException("Failed to fetch credits. Status code: " + response.statusCode());
        }
        return objectMapper.readValue(response.body(), CreditsDTO.class);
    }

    // --- DENNE METODE MANGLER DU ---
    /**
     * Henter den komplette liste af filmgenrer fra TMDb.
     * @return Et DTO-objekt, der indeholder en liste af alle genrer.
     */
    public GenreApiResultDTO fetchGenres() throws IOException, InterruptedException {
        String url = String.format("%s/genre/movie/list?api_key=%s&language=da-DK", BASE_URL, API_KEY);
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IOException("Failed to fetch genres. Status code: " + response.statusCode());
        }
        return objectMapper.readValue(response.body(), GenreApiResultDTO.class);
    }
}