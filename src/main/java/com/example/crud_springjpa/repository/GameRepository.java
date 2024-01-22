package com.example.crud_springjpa.repository;

import com.example.crud_springjpa.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface GameRepository extends JpaRepository<Game, Long> {
    // Find games by a specific genre name
    List<Game> findByGenresName(String genreName);

    // Find games by platform name
    List<Game> findByPlatformsName(String platformName);

    // Find games released after a specific date
    List<Game> findByReleaseDateAfter(LocalDate date);

    // Combine genre and platform in a single query
    List<Game> findByGenresNameAndPlatformsName(String genreName, String platformName);

    // Additional chaining example: Find games by genre and released after a specific date
    List<Game> findByGenresNameAndReleaseDateAfter(String genreName, LocalDate date);
    void deleteByTitle(String title);
    List<Game> findByTitleContaining(String nameFragment);
}
