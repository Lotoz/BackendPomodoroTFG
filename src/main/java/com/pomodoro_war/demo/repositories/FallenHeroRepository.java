package com.pomodoro_war.demo.repositories;

import com.pomodoro_war.demo.entities.lore.FallenHero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FallenHeroRepository extends JpaRepository<FallenHero, Long> {
    // Devuelve el cementerio ordenado cronológicamente, mostrando los caídos más recientes primero
    List<FallenHero> findByUserUsernameOrderByFallenAtDesc(String username);

    @Modifying
    @Query("DELETE FROM FallenHero f WHERE f.user.username = :username")
    void deleteAllByUsername(@Param("username") String username);
}