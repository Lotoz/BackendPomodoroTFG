package com.pomodoro_war.demo.security.dtos;
import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String email;
    private String code;
    private String newPassword;
}