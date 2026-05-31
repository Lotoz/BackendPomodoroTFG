package com.pomodoro_war.demo.entities.lore;

import com.pomodoro_war.demo.entities.auth.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "graveyard")
public class FallenHero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String heroClass; // "Caballero", "Mago", "Clérigo", etc.
    private Integer level;

    @Column(length = 500)
    private String deathReason; // Ej: "Abatido por el Dragón de la Procrastinación en el nivel 5 de la Zona de Lava"

    private LocalDateTime fallenAt; // Fecha y hora de la muerte

    // Vinculamos la tumba al jugador para que cada usuario solo vea sus propios caídos
    @ManyToOne
    @JoinColumn(name = "username")
    private User user;
}