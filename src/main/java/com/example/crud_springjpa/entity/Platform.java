package com.example.crud_springjpa.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Platform {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @ManyToMany(mappedBy = "platforms")
    private Set<Game> games = new HashSet<>();

    public Platform(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}