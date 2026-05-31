package com.pomodoro_war.demo.mappers;

import com.pomodoro_war.demo.dtos.response.TaskResponse;
import com.pomodoro_war.demo.entities.pomodoro.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {
    public TaskResponse toDto(Task entity) {
        return new TaskResponse(
                entity.getId(),
                entity.getName(),
                entity.isCompleted(),
                entity.getDescription(),
                entity.getDate()
        );
    }
}