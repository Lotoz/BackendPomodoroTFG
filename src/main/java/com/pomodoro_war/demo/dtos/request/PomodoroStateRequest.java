package com.pomodoro_war.demo.dtos.request;

import com.pomodoro_war.demo.entities.enums.TypeCycle;
import lombok.Data;

@Data
public class PomodoroStateRequest {
    private TypeCycle typeCycle; // WORK, BREAK, LONG_BREAK
    private boolean active;
}