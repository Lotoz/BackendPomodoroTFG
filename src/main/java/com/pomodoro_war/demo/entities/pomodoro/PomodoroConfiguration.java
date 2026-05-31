package com.pomodoro_war.demo.entities.pomodoro;

import com.pomodoro_war.demo.entities.auth.User;
import com.pomodoro_war.demo.entities.enums.TypeCycle;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pomodoro_configurations")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PomodoroConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Configuraciones de tiempo (en minutos)
    private int workDurationMinutes = 25;
    private int shortBreakDurationMinutes = 5;
    private int longBreakDurationMinutes = 15;

    // Cantidad de ciclos antes del descanso largo
    private int cyclesBeforeLongBreak = 4;

    // Enumerador guardado como texto en la BD para que sea legible
    @Enumerated(EnumType.STRING)
    private TypeCycle typeCycle = TypeCycle.WORK;

    private boolean active = true;

    // Relación directa: Esta configuración pertenece a un usuario concreto
    @OneToOne
    @JoinColumn(name = "username") // Esta será la clave foránea en la tabla
    private User user;
}