package com.example.crud_springjpa.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Genre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "genre_name")
    private String name;

    @ManyToMany(mappedBy = "genres", fetch = FetchType.EAGER)
    private Set<Game> games = new HashSet<>();

    public Genre(String name) {
        this.name = name;
    }

    public void addGame(Game game) {
        this.games.add(game);
        game.getGenres().add(this);
    }

    @Override
    public String toString() {
        return this.name;
    }
}