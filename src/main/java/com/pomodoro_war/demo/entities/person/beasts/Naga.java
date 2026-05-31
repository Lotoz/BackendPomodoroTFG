package com.pomodoro_war.demo.entities.person.beasts;

import com.pomodoro_war.demo.entities.person.Beast;
import com.pomodoro_war.demo.entities.person.Person;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("Naga")
@NoArgsConstructor
public class Naga extends Beast {

    public Naga(String name, int life, int armor) {
        super(name, life, armor);
    }

    @Override
    public int attack(Person enemy) {
        int damage = throwDices();

        // Simplemente aplica el veneno.
        // Si el enemigo es un Elfo, su propio código anulará el daño y romperá su armadura.
        if (!enemy.isPoisoned()) {
            enemy.setPoisoned(true);
            enemy.setPoisonTurns(3);
        }

        return damage;
    }
}