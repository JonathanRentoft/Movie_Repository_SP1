package app.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class PersonDAO<T, ID> implements CrudDAO<T, ID> {
    private static EntityManagerFactory emf;
    private Class<T> entityClass;

    public PersonDAO(Class<T> entityClass, EntityManagerFactory _emf) {
        this.entityClass = entityClass;
        emf = _emf;
    }

    @Override
    public void save(T t) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(t);
            em.getTransaction().commit();
        }
    }

    @Override
    public T findById(ID id) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.find(entityClass, id);
        }
    }

    // Implementer de andre metoder efter behov...
    @Override
    public List<T> getAll() { return null; }
    @Override
    public void update(T t) { }
    @Override
    public void delete(ID id) { }
}