package com.pomodoro_war.demo.dtos.response;

import com.pomodoro_war.demo.entities.enums.ZoneType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BestiaryResponse {
    private Long id;
    private String name;
    private ZoneType zone;
    private String description;
    private String photo;
    private String team;
}