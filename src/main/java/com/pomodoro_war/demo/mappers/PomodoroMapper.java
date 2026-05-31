package com.pomodoro_war.demo.mappers;

import com.pomodoro_war.demo.dtos.response.PomodoroConfigResponse;
import com.pomodoro_war.demo.entities.pomodoro.PomodoroConfiguration;
import org.springframework.stereotype.Component;

@Component
public class PomodoroMapper {

    public PomodoroConfigResponse toDto(PomodoroConfiguration entity) {
        if (entity == null) {
            return null;
        }

        return new PomodoroConfigResponse(
                entity.getWorkDurationMinutes(),
                entity.getShortBreakDurationMinutes(),
                entity.getLongBreakDurationMinutes(),
                entity.getCyclesBeforeLongBreak(),
                entity.getTypeCycle(),
                entity.isActive()
        );
    }
}