package com.pomodoro_war.demo.dtos.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PomodoroSettingsRequest {
    private int workDurationMinutes;
    private int shortBreakDurationMinutes;
    private int longBreakDurationMinutes;
    private int cyclesBeforeLongBreak;
}
