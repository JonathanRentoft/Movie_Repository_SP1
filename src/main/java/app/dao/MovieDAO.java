package app.dao;

import app.entities.Movie;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class MovieDAO {

    public void save(Movie movie, EntityManager em) {
        em.persist(movie);
    }

    public Movie findById(long id, EntityManager em) {
        return em.find(Movie.class, id);
    }

    public List<Movie> getAll(EntityManager em) {
        TypedQuery<Movie> query = em.createQuery("SELECT m FROM Movie m", Movie.class);
        return query.getResultList();
    }

    public Movie update(Movie movie, EntityManager em) {
        return em.merge(movie);
    }

    public void delete(long id, EntityManager em) {
        // Først findes filmen for at sikre, at den er "managed" af EntityManager'en.
        Movie movieToDelete = findById(id, em);
        if (movieToDelete != null) {
            em.remove(movieToDelete);
        }
    }
    public List<Movie> getMoviesByGenre(String genreName, EntityManager em) {
        TypedQuery<Movie> query = em.createQuery("SELECT m FROM Movie m JOIN m.genres g WHERE g.name = :genreName", Movie.class);
        query.setParameter("genreName", genreName);
        return query.getResultList();
    }

    // Søger efter filmtitler (case-insensitive)
    public List<Movie> searchByTitle(String searchString, EntityManager em) {
        TypedQuery<Movie> query = em.createQuery("SELECT m FROM Movie m WHERE lower(m.title) LIKE lower(:search)", Movie.class);
        query.setParameter("search", "%" + searchString + "%");
        return query.getResultList();
    }

    // Henter de 10 højest ratede film
    public List<Movie> getTopRated(int count, EntityManager em) {
        TypedQuery<Movie> query = em.createQuery("SELECT m FROM Movie m ORDER BY m.rating DESC", Movie.class);
        query.setMaxResults(count);
        return query.getResultList();
    }

    // Henter de 10 mest populære film
    public List<Movie> getMostPopular(int count, EntityManager em) {
        TypedQuery<Movie> query = em.createQuery("SELECT m FROM Movie m ORDER BY m.popularity DESC", Movie.class);
        query.setMaxResults(count);
        return query.getResultList();
    }

    // Udregner den gennemsnitlige rating for alle film
    public double getAverageRating(EntityManager em) {
        TypedQuery<Double> query = em.createQuery("SELECT avg(m.rating) FROM Movie m", Double.class);
        return query.getSingleResult();
    }
    public List<Movie> getLowestRated(int count, EntityManager em) {
        // Bemærk: Vi sorterer ASC (Ascending) i stedet for DESC (Descending)
        TypedQuery<Movie> query = em.createQuery("SELECT m FROM Movie m ORDER BY m.rating ASC", Movie.class);
        query.setMaxResults(count);
        return query.getResultList();
    }


}