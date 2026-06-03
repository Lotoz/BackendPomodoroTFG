package com.pomodoro_war.demo.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserAdminResponse {
    private String username;
    private String email;
    private String masterName;
    private String role;
}