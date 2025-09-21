package app.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "actors")
@Getter
@Setter
@NoArgsConstructor
public class Actor {

    @Id
    private Long id;

    @Column(nullable = false)
    private String name;

    // Vi definerer den anden side af Many-to-Many relationen
    // 'mappedBy = "actors"' refererer til vores 'actors' feltet i Movie-klassen.
    @ManyToMany(mappedBy = "actors")
    private Set<Movie> movies = new HashSet<>();

    public Actor(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}