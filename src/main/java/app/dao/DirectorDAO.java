package app.dao;

import app.entities.Director;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class DirectorDAO {

    public Director findById(long id, EntityManager em) {
        return em.find(Director.class, id);
    }

    public Director save(Director director, EntityManager em) {
        return em.merge(director);
    }
    public List<Director> getAll(EntityManager em) {
        TypedQuery<Director> query = em.createQuery("SELECT a FROM Actor a", Director.class);
        return query.getResultList();
    }
}