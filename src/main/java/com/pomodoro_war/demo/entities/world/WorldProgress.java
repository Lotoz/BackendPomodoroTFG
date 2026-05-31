package com.pomodoro_war.demo.entities.world;

import com.pomodoro_war.demo.entities.auth.User;
import com.pomodoro_war.demo.entities.enums.ZoneType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "world_progress")
public class WorldProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Usamos nuestro nuevo Enum y lo guardamos como texto en SQLite
    @Enumerated(EnumType.STRING)
    private ZoneType currentZone;

    // Nivel o número de batalla dentro de esa zona
    private Integer currentStage;

    @OneToOne
    @JoinColumn(name = "username", referencedColumnName = "username")
    private User user;
}