package com.pomodoro_war.demo.dtos.response;

import com.pomodoro_war.demo.entities.person.Hero;
import lombok.Data;

@Data
public class TavernHeroDto {
    private String name;
    private String heroClass;
    private int life;
    private int armor;

    // Constructor para mapear fácilmente desde la entidad
    public TavernHeroDto(Hero hero) {
        this.name = hero.getName();
        this.heroClass = hero.getClass().getSimpleName();
        this.life = hero.getLifeMax(); // Vida base
        this.armor = hero.getArmor();
    }
}