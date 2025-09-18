package app.dao;

import app.entities.Genre;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class GenreDAO {

    public Genre findById(long id, EntityManager em) {
        return em.find(Genre.class, id);
    }

    public void save(Genre genre, EntityManager em) {
        em.merge(genre);
    }
    public List<Genre> getAll(EntityManager em) {
        TypedQuery<Genre> query = em.createQuery("SELECT a FROM Actor a", Genre.class);
        return query.getResultList();
    }
}