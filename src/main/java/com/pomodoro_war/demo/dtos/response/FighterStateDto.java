package com.pomodoro_war.demo.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FighterStateDto {
    private Long id;
    private String name;
    private String type;
    private String fighterClass;
    private int currentLife;
    private int maxLife;
    private int armor; 
    private boolean isStunned;
    private boolean isPoisoned;
    private boolean isAlive;
}