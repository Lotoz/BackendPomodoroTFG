package com.pomodoro_war.demo.repositories;

import com.pomodoro_war.demo.entities.person.Beast;
import com.pomodoro_war.demo.entities.person.Hero;
import com.pomodoro_war.demo.entities.person.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    @Query("SELECT h FROM Hero h WHERE h.user.username = :username AND h.state = true AND h.inActivateTeam = true")
    List<Hero> findActiveHeroesByUsername(@Param("username") String username);

    @Query("SELECT b FROM Beast b WHERE b.user.username = :username AND b.state = true")
    List<Beast> findActiveBeastsByUsername(@Param("username") String username);

    @Query("SELECT COUNT(h) FROM Hero h WHERE h.user.username = :username AND h.state = true")
    int countActiveHeroesByUsername(@Param("username") String username);
    
    @Query("SELECT h FROM Hero h WHERE h.user.username = :username AND h.state = true")
    List<Hero> findAllAliveHeroesByUsername(@Param("username") String username);
    
    @Query("SELECT COUNT(h) FROM Hero h WHERE h.user.username = :username AND h.state = true AND h.inActivateTeam = true")
    int countActiveTeamHeroesByUsername(@Param("username") String username);

    @Modifying
    @Query("DELETE FROM Person p WHERE p.user.username = :username")
    void deleteAllByUsername(@Param("username") String username);
}
