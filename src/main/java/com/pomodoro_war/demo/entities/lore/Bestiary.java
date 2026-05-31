package com.pomodoro_war.demo.entities.lore;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Bestiary")
public class Bestiary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String zone;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Column(length = 1000)
    private String photo;
    private String team; //Si es hero o bestia

}
