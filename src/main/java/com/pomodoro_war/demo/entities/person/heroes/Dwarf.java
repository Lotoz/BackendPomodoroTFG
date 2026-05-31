package com.pomodoro_war.demo.entities.person.heroes;

import com.pomodoro_war.demo.entities.person.Hero;
import com.pomodoro_war.demo.entities.person.Person;
import com.pomodoro_war.demo.entities.person.beasts.Goblins;
import com.pomodoro_war.demo.entities.person.beasts.Orcus;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("Dwarf")
@NoArgsConstructor
public class Dwarf extends Hero {

    public Dwarf(String name, int life, int armor) {
        super(name, life, armor);
    }

    @Override
    public int attack(Person enemy) {
        int damage = throwDices();

        if (enemy instanceof Orcus) {
            // Penalización: Ataca con un 20% menos de fuerza a los Orcus
            damage -= (int) (damage * 0.2);
        } else if (enemy instanceof Goblins) {
            // Bonificación: Ataca con +20 de fuerza plana a los Goblins
            damage += 20;
        }

        // Aseguramos que el daño nunca sea negativo tras una penalización
        return Math.max(0, damage);
    }
}
