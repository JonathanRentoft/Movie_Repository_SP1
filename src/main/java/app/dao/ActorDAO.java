package app.dao;

import app.entities.Actor;
import jakarta.persistence.EntityManager;

public class ActorDAO {

    public Actor findById(long id, EntityManager em) {
        return em.find(Actor.class, id);
    }

    public Actor save(Actor actor, EntityManager em) {
        return em.merge(actor);
    }
}