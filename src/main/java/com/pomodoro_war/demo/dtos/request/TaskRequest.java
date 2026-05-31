package com.pomodoro_war.demo.dtos.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;


@Data
public class TaskRequest {
    private String titulo;
    private Boolean completada;
    private String description;
    private LocalDate date;
}