package app.dao;

import app.entities.Genre;
import jakarta.persistence.EntityManager;

public class GenreDAO {

    public Genre findById(long id, EntityManager em) {
        return em.find(Genre.class, id);
    }

    public void save(Genre genre, EntityManager em) {
        em.merge(genre);
    }
}