package app.dao;

import app.entities.Actor;
import app.entities.Director;
import app.entities.Genre;
import app.entities.Movie;
import jakarta.persistence.EntityManager;
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
class MovieDAOTest {

    private static MovieDAO movieDAO;
    private static EntityManagerFactory emf;

    // Opretter en Docker container med en PostgreSQL database før alle tests køres
    @Container
    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:15-alpine");

    @BeforeAll
    static void setUpAll() {
        // Konfigurerer Hibernate til at bruge den midlertidige test-database
        Configuration configuration = new Configuration();
        configuration.setProperty("hibernate.connection.url", postgresContainer.getJdbcUrl());
        configuration.setProperty("hibernate.connection.username", postgresContainer.getUsername());
        configuration.setProperty("hibernate.connection.password", postgresContainer.getPassword());
        configuration.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
        configuration.setProperty("hibernate.hbm2ddl.auto", "create");

        // Tilføj entity klasser
        configuration.addAnnotatedClass(Movie.class);
        configuration.addAnnotatedClass(Genre.class);
        configuration.addAnnotatedClass(Actor.class);
        configuration.addAnnotatedClass(Director.class);

        emf = configuration.buildSessionFactory().unwrap(EntityManagerFactory.class);
        movieDAO = new MovieDAO();
    }

    @BeforeEach
    void setUp() {
        // Sletter og genskaber databasen før HVER test
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            // Slet data i den rigtige rækkefølge pga. foreign key constraints
            em.createQuery("DELETE FROM Movie m").executeUpdate();
            em.createQuery("DELETE FROM Genre g").executeUpdate();
            em.getTransaction().commit();
        }
    }

    @Test
    @DisplayName("Test: Gem en film og find den via ID")
    void saveAndFindById() {
        try (EntityManager em = emf.createEntityManager()) {
            Movie movie = new Movie(1L, "Test Film", "Oversigt", LocalDate.now(), 8.5, 100.0, "/path.jpg");

            em.getTransaction().begin();
            movieDAO.save(movie, em);
            em.getTransaction().commit();

            Movie foundMovie = movieDAO.findById(1L, em);
            assertNotNull(foundMovie);
            assertEquals("Test Film", foundMovie.getTitle());
        }
    }

    @Test
    @DisplayName("Test: Hent alle film")
    void getAll() {
        try (EntityManager em = emf.createEntityManager()) {
            Movie m1 = new Movie(1L, "Film 1", "Oversigt", LocalDate.now(), 8.0, 100.0, "/path1.jpg");
            Movie m2 = new Movie(2L, "Film 2", "Oversigt", LocalDate.now(), 7.0, 110.0, "/path2.jpg");

            em.getTransaction().begin();
            movieDAO.save(m1, em);
            movieDAO.save(m2, em);
            em.getTransaction().commit();

            List<Movie> allMovies = movieDAO.getAll(em);
            assertEquals(2, allMovies.size());
        }
    }

    @Test
    @DisplayName("Test: Opdater en film")
    void update() {
        try (EntityManager em = emf.createEntityManager()) {
            Movie movie = new Movie(1L, "Gammel Titel", "Oversigt", LocalDate.now(), 8.5, 100.0, "/path.jpg");

            em.getTransaction().begin();
            movieDAO.save(movie, em);
            em.getTransaction().commit();

            movie.setTitle("Ny Titel");

            em.getTransaction().begin();
            movieDAO.update(movie, em);
            em.getTransaction().commit();

            Movie updatedMovie = movieDAO.findById(1L, em);
            assertEquals("Ny Titel", updatedMovie.getTitle());
        }
    }

    @Test
    @DisplayName("Test: Søg efter titel (case-insensitive)")
    void searchByTitle() {
        try (EntityManager em = emf.createEntityManager()) {
            Movie m1 = new Movie(1L, "Den Store Test", "Oversigt", LocalDate.now(), 8.0, 100.0, "/path1.jpg");
            Movie m2 = new Movie(2L, "En Anden Film", "Oversigt", LocalDate.now(), 7.0, 110.0, "/path2.jpg");

            em.getTransaction().begin();
            movieDAO.save(m1, em);
            movieDAO.save(m2, em);
            em.getTransaction().commit();

            List<Movie> results = movieDAO.searchByTitle("store test", em);
            assertEquals(1, results.size());
            assertEquals("Den Store Test", results.get(0).getTitle());
        }
    }

    @Test
    @DisplayName("Test: Hent top 2 ratede film")
    void getTopRated() {
        try (EntityManager em = emf.createEntityManager()) {
            Movie m1 = new Movie(1L, "Høj Rating", "Oversigt", LocalDate.now(), 9.0, 100.0, "/path1.jpg");
            Movie m2 = new Movie(2L, "Mellem Rating", "Oversigt", LocalDate.now(), 7.5, 110.0, "/path2.jpg");
            Movie m3 = new Movie(3L, "Lav Rating", "Oversigt", LocalDate.now(), 6.0, 120.0, "/path3.jpg");

            em.getTransaction().begin();
            movieDAO.save(m1, em);
            movieDAO.save(m2, em);
            movieDAO.save(m3, em);
            em.getTransaction().commit();

            List<Movie> topMovies = movieDAO.getTopRated(2, em);
            assertEquals(2, topMovies.size());
            assertEquals(9.0, topMovies.get(0).getRating()); // Første film skal have højest rating
            assertEquals(7.5, topMovies.get(1).getRating());
        }
    }
}