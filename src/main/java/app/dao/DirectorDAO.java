package app.dao;

import app.entities.Director;
import jakarta.persistence.EntityManager;

public class DirectorDAO {

    public Director findById(long id, EntityManager em) {
        return em.find(Director.class, id);
    }

    public Director save(Director director, EntityManager em) {
        return em.merge(director);
    }
}