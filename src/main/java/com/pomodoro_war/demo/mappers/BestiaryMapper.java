package com.pomodoro_war.demo.mappers;

import com.pomodoro_war.demo.dtos.response.BestiaryResponse;
import com.pomodoro_war.demo.entities.lore.Bestiary;
import org.springframework.stereotype.Component;

@Component
public class BestiaryMapper {
    public BestiaryResponse toDto(Bestiary entity) {
        return new BestiaryResponse(
                entity.getId(),
                entity.getName(),
                entity.getZone(),
                entity.getDescription(),
                entity.getPhoto(),
                entity.getTeam()
        );
    }
}