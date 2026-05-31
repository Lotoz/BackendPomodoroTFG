package com.pomodoro_war.demo.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
public class FallenHeroResponse {
    private Long id;
    private String name;
    private String heroClass;
    private Integer level;
    private String deathReason;
    private String fallenDate;

    public FallenHeroResponse(com.pomodoro_war.demo.entities.lore.FallenHero entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.heroClass = entity.getHeroClass();
        this.level = entity.getLevel();
        this.deathReason = entity.getDeathReason();
        this.fallenDate = entity.getFallenAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
}