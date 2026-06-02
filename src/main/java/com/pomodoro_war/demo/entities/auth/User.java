package com.pomodoro_war.demo.entities.auth;

import com.pomodoro_war.demo.entities.pomodoro.PomodoroConfiguration;
import com.pomodoro_war.demo.entities.pomodoro.Task;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "usuarios")
public class User {

    @Id
    private String username;

    private String password;
    private String email;
    private String masterName;
    private String resetCode;

    //Rol de usuario para permisos
    private String role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private PomodoroConfiguration pomodoroConfiguration;
}