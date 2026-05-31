package com.pomodoro_war.demo.dtos.response;


import com.pomodoro_war.demo.entities.enums.CombatStatus;
import com.pomodoro_war.demo.entities.enums.ZoneType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CombatStateResponse {

    // Estado global de la partida
    private CombatStatus status;
    private ZoneType currentZone;
    private Integer currentStage; // 1 al 10

    // Participantes actualizados
    private List<FighterStateDto> heroes;
    private List<FighterStateDto> beasts;

    // Narrativa de la ronda (Ej: "El Orco golpeó al Caballero por 20")
    private List<String> combatLogs;
}