package com.pomodoro_war.demo.security.dtos;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}