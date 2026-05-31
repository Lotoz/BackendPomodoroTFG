package com.pomodoro_war.demo.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class TaskResponse {
    private Long id;

    // Exactamente los nombres que espera Vue
    private String titulo;
    private boolean completada;

    private String description;
    private LocalDate date;
}