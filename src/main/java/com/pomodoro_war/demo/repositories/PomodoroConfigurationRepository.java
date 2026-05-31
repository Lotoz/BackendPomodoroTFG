package com.pomodoro_war.demo.repositories;

import com.pomodoro_war.demo.entities.pomodoro.PomodoroConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PomodoroConfigurationRepository  extends JpaRepository<PomodoroConfiguration,Long> {
    Optional<PomodoroConfiguration> findByUserUsername(String username);
}
