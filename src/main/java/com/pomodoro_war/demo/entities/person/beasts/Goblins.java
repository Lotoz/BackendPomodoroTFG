package com.pomodoro_war.demo.entities.person.beasts;

import com.pomodoro_war.demo.entities.person.Beast;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("Goblins")
@NoArgsConstructor
public class Goblins extends Beast {

    public Goblins(String name, int life, int armor) {
        super(name, life, armor);
    }
}