package com.pomodoro_war.demo.entities.person.heroes;

import com.pomodoro_war.demo.entities.person.Hero;
import com.pomodoro_war.demo.entities.person.Person;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("Cleric")
@NoArgsConstructor
public class Cleric extends Hero {

    public Cleric(String name, int life, int armor) {
        super(name, life, armor);
    }

    // SOBRESCRIBIMOS para que solo tire 1 dado (de 0 a 100)
    @Override
    protected int throwDices() {
        return (int) (Math.random() * 101);
    }

    public void healTarget(Person target) {
        int healPower = throwDices() / 2;
        target.receiveHeal(healPower);
    }

    @Override
    public int attack(Person enemy) {
        return throwDices();
    }
}