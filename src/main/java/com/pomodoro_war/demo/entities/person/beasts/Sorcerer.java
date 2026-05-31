package com.pomodoro_war.demo.entities.person.beasts;

import com.pomodoro_war.demo.entities.person.Beast;
import com.pomodoro_war.demo.entities.person.Person;
import com.pomodoro_war.demo.entities.person.heroes.Dwarf;
import com.pomodoro_war.demo.entities.person.interfaces.Magic;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;
import java.util.Arrays;

@Entity
@DiscriminatorValue("Sorcerer")
@NoArgsConstructor
public class Sorcerer extends Beast implements Magic {

    public Sorcerer(String name, int life, int armor) {
        super(name, life, armor);
    }

    @Override
    public int attack(Person enemy) {
        int damage = throwDices();
        if (enemy instanceof Dwarf) damage += (int) (damage * 0.2);
        return damage;
    }

    @Override
    protected int throwDices() {
        int[] dice = {
                (int) (Math.random() * 101),
                (int) (Math.random() * 101),
                (int) (Math.random() * 101)
        };
        Arrays.sort(dice);
        return dice[1] + dice[2];
    }

    @Override
    public void applicationStun(Person character) {
        if (Math.random() > 0.5) {
            character.setStun(true);
            character.setTimeStun(0); // Reiniciamos el contador
        }
    }
}