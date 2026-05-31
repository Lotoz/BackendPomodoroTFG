package com.pomodoro_war.demo.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BestiaryResponse {
    private Long id;
    private String name;
    private String zone;
    private String description;
    private String photo;
    private String team;
}