package com.pomodoro_war.demo.dtos.response;

import com.pomodoro_war.demo.entities.enums.TypeCycle;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PomodoroConfigResponse {
    private int workDurationMinutes;
    private int shortBreakDurationMinutes;
    private int longBreakDurationMinutes;
    private int cyclesBeforeLongBreak;
    private TypeCycle typeCycle;
    private boolean active;
}
