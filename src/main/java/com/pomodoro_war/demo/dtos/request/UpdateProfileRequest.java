package com.pomodoro_war.demo.dtos.request;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String email;
    private String masterName;
}
