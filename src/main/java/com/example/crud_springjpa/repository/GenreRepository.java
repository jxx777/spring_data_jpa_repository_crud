package com.example.crud_springjpa.repository;

import com.example.crud_springjpa.entity.Genre;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GenreRepository extends JpaRepository<Genre, Long> {
    // Find genres by a part of the name (like search)
    List<Genre> findByNameContaining(String nameFragment);
    List<Genre> findAllByOrderByNameAsc();
    List<Genre> findAllByOrderByNameDesc();
    void deleteByName(String name);
    long countByName(String name);
    @Query("SELECT g FROM Genre g WHERE g.name LIKE %:name%")
    List<Genre> searchByName(@Param("name") String name);
}
