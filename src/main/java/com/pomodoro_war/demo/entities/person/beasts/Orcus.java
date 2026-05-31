package com.pomodoro_war.demo.entities.person.beasts;

import com.pomodoro_war.demo.entities.person.Beast;
import com.pomodoro_war.demo.entities.person.Person;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("Orcus")
@NoArgsConstructor
public class Orcus extends Beast {

    public Orcus(String name, int life, int armor) {
        super(name, life, armor);
    }

    @Override
    public int attack(Person enemy) {
        int damage = throwDices();

        // Reducimos la armadura del enemigo permanentemente
        int currentArmor = enemy.getArmor();
        enemy.setArmor(Math.max(0, currentArmor - 2));

        return damage;
    }
}