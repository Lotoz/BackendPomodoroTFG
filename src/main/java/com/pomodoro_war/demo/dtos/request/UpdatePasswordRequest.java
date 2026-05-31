package com.pomodoro_war.demo.dtos.request;

import lombok.Data;

@Data
public class UpdatePasswordRequest {
    private String currentPassword;
    private String newPassword;
    private String confirmPassword;
}