package com.pomodoro_war.demo.repositories;
import com.pomodoro_war.demo.entities.world.WorldProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorldProgressRepository extends JpaRepository<WorldProgress, Long> {
    // Busca el progreso del mundo basándose en el username del usuario asociado
    Optional<WorldProgress> findByUserUsername(String username);

    @Modifying
    @Query("DELETE FROM WorldProgress w WHERE w.user.username = :username")
    void deleteAllByUsername(@Param("username") String username);
}