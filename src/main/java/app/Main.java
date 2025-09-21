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
        // SETUP
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        MovieFetcher fetcher = new MovieFetcher();
        MovieService movieService = new MovieService(emf);

        // Sørger for at vores 'genres' tabel er fyldt, før vi gemmer film.
        movieService.populateGenres();

        // Hent første side for at vide, hvor mange sider der er i alt
                MovieApiResultDTO firstPage = fetcher.fetchMovies(1);
        int totalPages = firstPage.getTotalPages();
        System.out.println("Total amount pages to process: " + totalPages);

        // Først behandler vi filmene fra den side, vi allerede har hentet
        System.out.println("Processing page 1 out of " + totalPages);
        for (MovieDTO movieDTO : firstPage.getResults()) {
            processAndSaveMovie(movieDTO, fetcher, movieService);
        }

        // husk at ændre 5 til totalPages inden aflevering ;) i=2 fordi vi allerede har processeret side 1 for at finde det fulde antal af sider.
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
        emf.close(); // vigtigt at lukke forbindelsen når programmet er færdigt ;)
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