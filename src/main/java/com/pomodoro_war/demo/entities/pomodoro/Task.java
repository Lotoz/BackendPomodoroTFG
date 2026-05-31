package com.pomodoro_war.demo.entities.pomodoro;

import com.pomodoro_war.demo.entities.auth.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "tareas")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private LocalDate date;
    private boolean completed;

    // Muchas tareas pertenecen a un solo usuario
    @ManyToOne
    @JoinColumn(name = "username") // Esta será la clave foránea en la tabla
    private User user;
}