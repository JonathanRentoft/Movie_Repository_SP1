package app.service;

import app.dao.ActorDAO;
import app.dao.DirectorDAO;
import app.dao.GenreDAO;
import app.dao.MovieDAO;
import app.dto.*;
import app.entities.Actor;
import app.entities.Director;
import app.entities.Genre;
import app.entities.Movie;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.io.IOException;

public class MovieService {
    private EntityManagerFactory emf;
    private MovieDAO movieDAO = new MovieDAO();

    // -- ÆNDRING HER: Fra GenericDAO til konkrete DAOs --
    private ActorDAO actorDAO = new ActorDAO();
    private DirectorDAO directorDAO = new DirectorDAO();
    private GenreDAO genreDAO = new GenreDAO();

    public MovieService(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public void populateGenres() {
        System.out.println("Populating genres table...");
        try {
            MovieFetcher fetcher = new MovieFetcher();
            GenreApiResultDTO genreResult = fetcher.fetchGenres();
            try (EntityManager em = emf.createEntityManager()) {
                em.getTransaction().begin();
                for (GenreDTO genreDTO : genreResult.getGenres()) {
                    // Bruger nu den konkrete GenreDAO
                    if (genreDAO.findById(genreDTO.getId(), em) == null) {
                        Genre genre = new Genre((long) genreDTO.getId(), genreDTO.getName());
                        genreDAO.save(genre, em);
                    }
                }
                em.getTransaction().commit();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void saveMovieFromDTOs(MovieDTO movieDTO, CreditsDTO creditsDTO) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            if (movieDAO.findById(movieDTO.getId(), em) != null) {
                em.getTransaction().rollback();
                return;
            }

            Movie movie = new Movie();
            movie.setId((long) movieDTO.getId());
            movie.setTitle(movieDTO.getTitle());
            movie.setOverview(movieDTO.getOverview());
            movie.setReleaseDate(movieDTO.getReleaseDate());
            movie.setRating(movieDTO.getRating());
            movie.setPopularity(movieDTO.getPopularity());
            movie.setPosterPath(movieDTO.getPosterPath());

            if (movieDTO.getGenreIds() != null) {
                for (Integer genreId : movieDTO.getGenreIds()) {
                    Genre genre = genreDAO.findById(genreId.longValue(), em);
                    if (genre != null) {
                        movie.addGenre(genre);
                    }
                }
            }

            for (ActorDTO actorDTO : creditsDTO.getActors()) {
                // Opret altid et "detached" objekt ud fra DTO'en
                Actor detachedActor = new Actor((long) actorDTO.getId(), actorDTO.getName());
                // Lad 'merge' finde ud af, om den skal oprettes eller opdateres,
                // og få den "managed" version tilbage.
                Actor managedActor = actorDAO.save(detachedActor, em);
                movie.addActor(managedActor);
            }

            creditsDTO.getCrew().stream()
                    .filter(c -> "Director".equals(c.getJob()))
                    .findFirst()
                    .ifPresent(crewMemberDTO -> {
                        Director detachedDirector = new Director((long) crewMemberDTO.getId(), crewMemberDTO.getName());
                        Director managedDirector = directorDAO.save(detachedDirector, em);
                        movie.addDirector(managedDirector);
                    });

            movieDAO.save(movie, em);

            em.getTransaction().commit();
            System.out.println("SAVED: " + movie.getTitle());
        } catch (Exception e) {
            System.err.println("Error saving movie '" + movieDTO.getTitle() + "'.");
            e.printStackTrace();
        }
    }
}