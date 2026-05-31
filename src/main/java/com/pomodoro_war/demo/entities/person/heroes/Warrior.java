package com.pomodoro_war.demo.entities.person.heroes;

import com.pomodoro_war.demo.entities.person.Hero;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("Warrior")
@NoArgsConstructor
public class Warrior extends Hero {

    public Warrior(String name, int life, int armor) {
        super(name, life, armor);
    }
}