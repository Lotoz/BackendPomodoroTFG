package com.pomodoro_war.demo.repositories;

import com.pomodoro_war.demo.entities.pomodoro.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository  extends JpaRepository<Task,Long> {
    List<Task> findByUserUsername(String username);
}
