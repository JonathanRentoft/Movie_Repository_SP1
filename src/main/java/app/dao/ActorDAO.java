package app.dao;

import app.entities.Actor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class ActorDAO {

    public Actor findById(long id, EntityManager em) {
        return em.find(Actor.class, id);
    }

    public Actor save(Actor actor, EntityManager em) {
        return em.merge(actor);
    }
    public List<Actor> getAll(EntityManager em) {
        TypedQuery<Actor> query = em.createQuery("SELECT a FROM Actor a", Actor.class);
        return query.getResultList();
    }
}