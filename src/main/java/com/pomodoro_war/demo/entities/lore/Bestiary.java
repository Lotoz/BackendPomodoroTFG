package com.pomodoro_war.demo.entities.lore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.Id;

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
    private String description;
    private String photo;
    private String team; //Si es hero o bestia

}
