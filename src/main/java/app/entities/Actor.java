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
    private Long id; // Brug TMDb's ID for at undgå dubletter

    @Column(nullable = false)
    private String name;

    // Vi definerer den anden side af Many-to-Many relationen
    // 'mappedBy = "actors"' refererer til 'actors' feltet i Movie-klassen.
    // Det fortæller Hibernate, at Movie-klassen "ejer" relationen,
    // og at join-tabellen allerede er defineret der.
    @ManyToMany(mappedBy = "actors")
    private Set<Movie> movies = new HashSet<>();

    // En god idé at have en constructor, der tager de vigtigste felter
    public Actor(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}