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

        // --- PRE-POPULATE (Once-only operation) ---
        // Sørger for at vores 'genres' tabel er fyldt, før vi gemmer film.
        movieService.populateGenres();

        // --- FETCH & SAVE LOOP ---
        System.out.println("\nFetching and saving ALL movies from the last 5 years...");

        // Hent første side for at vide, hvor mange sider der er i alt
        MovieApiResultDTO firstPage = fetcher.fetchMovies(1);
        int totalPages = firstPage.getTotalPages();
        System.out.println("Total pages to process: " + totalPages);

        // Først, behandl filmene fra den side, vi allerede har hentet
        System.out.println("Processing page 1 of " + totalPages);
        for (MovieDTO movieDTO : firstPage.getResults()) {
            processAndSaveMovie(movieDTO, fetcher, movieService);
        }

        // husk at ændre 5 til totalPages inden aflevering ;)
        for (int i = 2; i <= totalPages; i++) {
            System.out.println("Processing page " + i + " of " + totalPages);
            try {
                MovieApiResultDTO resultPage = fetcher.fetchMovies(i);
                for (MovieDTO movieDTO : resultPage.getResults()) {
                    processAndSaveMovie(movieDTO, fetcher, movieService);
                }
            } catch (Exception e) {
                System.err.println("Failed to process page " + i + ". Continuing to next page.");
                e.printStackTrace();
            }
        }

        System.out.println("\n--- Full data import complete. ---");
        emf.close(); // Luk forbindelsen når programmet er færdigt
    }

    /**
     * Helper-metode til at hente credits og gemme en enkelt film.
     * Dette undgår kode-gentagelse.
     */
    private static void processAndSaveMovie(MovieDTO movieDTO, MovieFetcher fetcher, MovieService movieService) {
        try {
            CreditsDTO creditsDTO = fetcher.fetchCredits(movieDTO.getId());
            movieService.saveMovieFromDTOs(movieDTO, creditsDTO);
        } catch (Exception e) {
            System.err.println("Failed to process movie: " + movieDTO.getTitle());
            e.printStackTrace();
        }
    }
}