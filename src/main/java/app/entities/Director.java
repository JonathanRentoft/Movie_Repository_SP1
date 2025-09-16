package app.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "directors")
@Getter
@Setter
@NoArgsConstructor
public class Director {

    @Id
    private Long id; // Brug TMDb's ID

    @Column(nullable = false)
    private String name;

    @ManyToMany(mappedBy = "directors")
    private Set<Movie> movies = new HashSet<>();

    public Director(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}