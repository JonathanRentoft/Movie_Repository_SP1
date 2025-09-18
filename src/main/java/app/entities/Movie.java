package app.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "movies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Movie {

    @Id
    private Long id;

    private String title;

    @Column(length = 2000)
    private String overview;

    private LocalDate releaseDate;
    private double rating;
    private double popularity;
    private String posterPath;

    public Movie(Long id, String title, String overview, LocalDate releaseDate, double rating, double popularity, String posterPath) {
        this.id = id;
        this.title = title;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.rating = rating;
        this.popularity = popularity;
        this.posterPath = posterPath;
    }
    // --- Relation til Genre ---
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "movie_genre",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres = new HashSet<>();

    // --- Relation til Actor ---
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "movie_actor",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "actor_id")
    )
    private Set<Actor> actors = new HashSet<>();

    // --- Relation til Director ---
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "movie_director",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "director_id")
    )
    private Set<Director> directors = new HashSet<>();


    public void addActor(Actor actor) {
        this.actors.add(actor);
        actor.getMovies().add(this);
    }

    public void addDirector(Director director) {
        this.directors.add(director);
        director.getMovies().add(this);
    }

    public void addGenre(Genre genre) {
        this.genres.add(genre);
        genre.getMovies().add(this);
    }
}