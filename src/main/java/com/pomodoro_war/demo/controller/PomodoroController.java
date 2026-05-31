package com.pomodoro_war.demo.controller;

import com.pomodoro_war.demo.dtos.request.PomodoroSettingsRequest;
import com.pomodoro_war.demo.dtos.request.PomodoroStateRequest;
import com.pomodoro_war.demo.dtos.response.FinishResponse;
import com.pomodoro_war.demo.dtos.response.PomodoroConfigResponse; // Importamos el nuevo DTO
import com.pomodoro_war.demo.services.PomodoroService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pomodoro")
@RequiredArgsConstructor
public class PomodoroController {

    private final PomodoroService pomodoroService;

    // Obtiene la configuración actual para cargar el temporizador
    @GetMapping("/config")
    public ResponseEntity<PomodoroConfigResponse> getConfiguration(Authentication authentication) {
        return ResponseEntity.ok(pomodoroService.getConfig(authentication.getName()));
    }

    // Modifica los tiempos personalizados del usuario
    @PutMapping("/settings")
    public ResponseEntity<PomodoroConfigResponse> updateSettings(@RequestBody PomodoroSettingsRequest request, Authentication authentication) {
        return ResponseEntity.ok(pomodoroService.updateSettings(authentication.getName(), request));
    }

    // Informa al servidor de en qué fase del ciclo se encuentra
    @PutMapping("/state")
    public ResponseEntity<PomodoroConfigResponse> updateState(@RequestBody PomodoroStateRequest request, Authentication authentication) {
        return ResponseEntity.ok(pomodoroService.updateState(authentication.getName(), request));
    }

    // Acción POST que se dispara al acabar un ciclo de trabajo
    @PostMapping("/finish")
    public ResponseEntity<FinishResponse> finishPomodoro(Authentication authentication) {
        return ResponseEntity.ok(pomodoroService.finishPomodoroCycle(authentication.getName()));
    }
}