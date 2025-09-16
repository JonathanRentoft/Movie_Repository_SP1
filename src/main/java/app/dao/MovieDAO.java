package app.dao;

import app.entities.Movie;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class MovieDAO {

    /**
     * Gemmer en ny Movie entity i databasen.
     * @param movie Det Movie-objekt, der skal gemmes.
     * @param em Den aktive EntityManager.
     */
    public void save(Movie movie, EntityManager em) {
        em.persist(movie);
    }

    /**
     * Finder en Movie baseret på dens primærnøgle (ID).
     * @param id ID'et for den film, der skal findes.
     * @param em Den aktive EntityManager.
     * @return Den fundne Movie, eller null hvis den ikke findes.
     */
    public Movie findById(long id, EntityManager em) {
        return em.find(Movie.class, id);
    }

    /**
     * Henter alle Movie entities fra databasen.
     * @param em Den aktive EntityManager.
     * @return En liste af alle film.
     */
    public List<Movie> getAll(EntityManager em) {
        TypedQuery<Movie> query = em.createQuery("SELECT m FROM Movie m", Movie.class);
        return query.getResultList();
    }

    /**
     * Opdaterer en eksisterende Movie entity i databasen.
     * @param movie Det opdaterede Movie-objekt.
     * @param em Den aktive EntityManager.
     * @return Den opdaterede, "managed" Movie entity.
     */
    public Movie update(Movie movie, EntityManager em) {
        return em.merge(movie);
    }

    /**
     * Sletter en Movie fra databasen baseret på dens ID.
     * @param id ID'et for den film, der skal slettes.
     * @param em Den aktive EntityManager.
     */
    public void delete(long id, EntityManager em) {
        // Først findes filmen for at sikre, at den er "managed" af EntityManager'en.
        Movie movieToDelete = findById(id, em);
        if (movieToDelete != null) {
            em.remove(movieToDelete);
        }
    }
}