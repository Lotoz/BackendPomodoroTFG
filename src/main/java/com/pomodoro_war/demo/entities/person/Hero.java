package com.pomodoro_war.demo.entities.person;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public abstract class Hero extends Person {

    public Hero(String name, int life, int armor) {
        super(name, life, armor);

    }

    @Override
    protected int throwDices() {
        int dice1 = (int) (Math.random() * 101);
        int dice2 = (int) (Math.random() * 101);
        return Math.max(dice1, dice2);
    }

    @Override
    public int attack(Person enemy) {
        return throwDices();
    }

    public void levelUp() {
        this.level += 1;

        // Aumentamos las estadísticas base
        this.lifeMax += 15; // Gana 15 de vida máxima por nivel
        this.life = this.lifeMax; // Se cura por completo al subir de nivel
        this.totalArmor += 9;
        this.armor = this.totalArmor; // Obtiene armadura total tambien
    }
}