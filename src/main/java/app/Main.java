package app;

import app.config.HibernateConfig;
import app.dto.CreditsDTO;
import app.dto.MovieApiResultDTO;
import app.dto.MovieDTO;
import app.service.MovieFetcher;
import app.service.MovieService;
import jakarta.persistence.EntityManagerFactory;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        // --- SETUP ---
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        MovieFetcher fetcher = new MovieFetcher();
        MovieService movieService = new MovieService(emf);

        System.out.println("Fetching and saving data from TMDb...");

        // --- FETCH & SAVE LOOP ---
        // Hent første side for at vide, hvor mange sider der er i alt
        MovieApiResultDTO firstPage = fetcher.fetchMovies(1);
        int totalPages = firstPage.getTotalPages();
        System.out.println("Total pages of movies: " + totalPages);

        // Loop gennem alle sider (eller færre for test)
        for (int i = 1; i <= 5; i++) { // Begræns stadig for test
            System.out.println("Processing page " + i);
            MovieApiResultDTO resultPage = fetcher.fetchMovies(i);

            for (MovieDTO movieDTO : resultPage.getResults()) {
                // For hver film, hent dens credits (skuespillere/instruktør)
                CreditsDTO creditsDTO = fetcher.fetchCredits(movieDTO.getId());

                // Brug MovieService til at konvertere og gemme
                movieService.saveMovieFromDTOs(movieDTO, creditsDTO);
            }
        }

        System.out.println("\n--- Data processing complete. ---");
    }
}