package com.pomodoro_war.demo.security.controller;


import com.pomodoro_war.demo.security.dtos.ForgotPasswordRequest;
import com.pomodoro_war.demo.security.dtos.LoginRequest;
import com.pomodoro_war.demo.security.dtos.RegisterRequest;
import com.pomodoro_war.demo.security.dtos.ResetPasswordRequest;
import com.pomodoro_war.demo.security.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        authService.requestPasswordReset(request.getEmail());
        return ResponseEntity.ok(Map.of("message", "Si el correo existe, se ha enviado un código."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(Map.of("message", "Contraseña restablecida con éxito. Ya puedes iniciar sesión."));
    }
}