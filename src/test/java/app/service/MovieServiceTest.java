package app.service;

import app.entities.Actor;
import app.entities.Director;
import app.entities.Genre;
import app.entities.Movie;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class MovieServiceTest {

    private static MovieService movieService;
    private static EntityManagerFactory emf;

    @Container
    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:15-alpine");

    @BeforeAll
    static void setUpAll() {
        // Samme setup som i DAO-testen
        Configuration configuration = new Configuration();
        configuration.setProperty("hibernate.connection.url", postgresContainer.getJdbcUrl());
        configuration.setProperty("hibernate.connection.username", postgresContainer.getUsername());
        configuration.setProperty("hibernate.connection.password", postgresContainer.getPassword());
        configuration.setProperty("hibernate.hbm2ddl.auto", "create");
        configuration.addAnnotatedClass(Movie.class);
        // Tilføj andre entities efter behov for mere komplekse tests
        configuration.addAnnotatedClass(Genre.class);
        configuration.addAnnotatedClass(Actor.class);
        configuration.addAnnotatedClass(Director.class);

        emf = configuration.buildSessionFactory().unwrap(EntityManagerFactory.class);
        movieService = new MovieService(emf);
    }

    @BeforeEach
    void setUp() {
        // Tøm databasen før hver test
    }

    @Test
    @DisplayName("Test: Service kan finde film via titel")
    void searchMovieByTitle() {
        // Opret en film direkte vha. JPA til test-setup
        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(new Movie(1L, "Jagten", "...", LocalDate.now(), 8.1, 120.0, "/j.jpg"));
            em.getTransaction().commit();
        }

        List<Movie> results = movieService.searchMovieByTitle("Jagten");
        assertFalse(results.isEmpty());
        assertEquals("Jagten", results.get(0).getTitle());
    }

    @Test
    @DisplayName("Test: Service kan udregne gennemsnitlig rating")
    void getAverageMovieRating() {
        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Movie").executeUpdate(); // Slet gamle film
            em.persist(new Movie(1L, "Film A", "...", LocalDate.now(), 6.0, 120.0, "/a.jpg"));
            em.persist(new Movie(2L, "Film B", "...", LocalDate.now(), 8.0, 120.0, "/b.jpg"));
            em.getTransaction().commit();
        }

        double average = movieService.getAverageMovieRating();
        assertEquals(7.0, average, 0.01); // 0.01 er en delta for præcision med doubles
    }
}