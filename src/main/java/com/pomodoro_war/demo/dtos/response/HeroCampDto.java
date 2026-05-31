package com.pomodoro_war.demo.dtos.response;

import com.pomodoro_war.demo.entities.person.Hero;
import lombok.Data;

@Data
public class HeroCampDto {
    private Long id;
    private String name;
    private String heroClass;
    private int level;
    private int life;
    private int lifeMax;
    private int armor;
    private boolean stun;
    private boolean poisoned;
    private boolean inActivateTeam;

    public HeroCampDto(Hero hero) {
        this.id = hero.getId();
        this.name = hero.getName();
        this.heroClass = hero.getClass().getSimpleName();
        this.level = hero.getLevel();
        this.life = hero.getLife();
        this.lifeMax = hero.getLifeMax();
        this.armor = hero.getArmor();
        this.stun = hero.isStun();
        this.poisoned = hero.isPoisoned();
        this.inActivateTeam = hero.isInActivateTeam(); // Mapeamos el estado
    }
}