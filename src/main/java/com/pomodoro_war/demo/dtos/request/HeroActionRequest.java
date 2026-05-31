package com.pomodoro_war.demo.dtos.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HeroActionRequest {
    private Long heroId;      // El héroe que ataca o cura
    private Long targetId;    // El enemigo (o aliado) objetivo
    private String actionType; // "ATTACK" o "HEAL"
}