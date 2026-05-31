package com.pomodoro_war.demo.repositories;

import com.pomodoro_war.demo.entities.lore.Bestiary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BestiaryRepository extends JpaRepository<Bestiary, Long> {
    List<Bestiary> findByTeam(String team);
}