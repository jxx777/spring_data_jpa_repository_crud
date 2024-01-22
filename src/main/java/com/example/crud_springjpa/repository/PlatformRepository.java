package com.example.crud_springjpa.repository;

import com.example.crud_springjpa.entity.Platform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PlatformRepository extends JpaRepository<Platform, Long> {
    // Find platforms by a part of the name (like search)
    Platform findByNameContaining(String nameFragment);

    // JPQL (Java Persistence Query Language) method
    @Query("SELECT COUNT(p) FROM Platform p WHERE p.games IS EMPTY")
    Long countPlatformsWithNoGames();
}
