package com.pomodoro_war.demo.entities.person.heroes;

import com.pomodoro_war.demo.entities.person.Hero;
import com.pomodoro_war.demo.entities.person.Person;
import com.pomodoro_war.demo.entities.person.beasts.Orcus;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("Elf")
@NoArgsConstructor
public class Elf extends Hero {

    @Transient
    private int originalArmor = -1;

    public Elf(String name, int life, int armor) {
        super(name, life, armor);
    }

    @Override
    public int attack(Person enemy) {
        int damage = throwDices();
        if (enemy instanceof Orcus) damage += 10;
        return damage;
    }

    // Magia orientada a objetos: Interceptamos cuando recibe veneno
    @Override
    public void setPoisoned(boolean isPoisoned) {
        super.setPoisoned(isPoisoned);

        if (isPoisoned) {
            if (this.originalArmor == -1) {
                this.originalArmor = this.armor; // Recordamos su armadura
            }
            this.armor = 0; // Destruimos la armadura
        } else {
            if (this.originalArmor != -1) {
                this.armor = this.originalArmor; // Recupera la armadura
                this.originalArmor = -1;
            }
        }
    }
}