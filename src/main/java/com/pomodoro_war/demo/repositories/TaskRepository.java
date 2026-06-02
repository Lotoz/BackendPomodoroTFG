package com.pomodoro_war.demo.repositories;

import com.pomodoro_war.demo.entities.pomodoro.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository  extends JpaRepository<Task,Long> {
    List<Task> findByUserUsername(String username);

    @Modifying
    @Query("DELETE FROM Task t WHERE t.user.username = :username")
    void deleteAllByUsername(@Param("username") String username);
}
