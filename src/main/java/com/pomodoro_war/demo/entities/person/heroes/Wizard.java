package com.pomodoro_war.demo.entities.person.heroes;

import com.pomodoro_war.demo.entities.person.Hero;
import com.pomodoro_war.demo.entities.person.Person;
import com.pomodoro_war.demo.entities.person.beasts.Goblins;
import com.pomodoro_war.demo.entities.person.interfaces.Magic;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("Wizard")
@NoArgsConstructor
public class Wizard extends Hero implements Magic {

    public Wizard(String name, int life, int armor) {
        super(name, life, armor);
    }

    @Override
    protected int throwDices() {
        // 3 dados estándar de 0 a 100
        int dice1 = (int) (Math.random() * 101);
        int dice2 = (int) (Math.random() * 101);
        int dice3 = (int) (Math.random() * 101);

        int valueMin = Math.min(dice1, Math.min(dice2, dice3));
        int valueMax = Math.max(dice1, Math.max(dice2, dice3));

        return valueMax + valueMin;
    }

    @Override
    public int attack(Person enemy) {
        int damage = throwDices();
        if (enemy instanceof Goblins) damage -= (int) (damage * 0.2);
        return damage;
    }

    @Override
    public void applicationStun(Person enemy) {
        enemy.setStun(true);
        enemy.setTimeStun(0);
    }
}