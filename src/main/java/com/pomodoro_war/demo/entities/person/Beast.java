package com.pomodoro_war.demo.entities.person;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public abstract class Beast extends Person {

    public Beast(String name, int life, int armor) {
        super(name, life, armor);
    }

    @Override
    protected int throwDices() {
        return (int)(Math.random() * 92);
    }

    @Override
    public int attack(Person enemy) {
        return throwDices();
    }
}
