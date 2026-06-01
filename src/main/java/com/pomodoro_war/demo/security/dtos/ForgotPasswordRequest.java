package com.pomodoro_war.demo.security.dtos;
import lombok.Data;

@Data
public class ForgotPasswordRequest {
    private String email;
}