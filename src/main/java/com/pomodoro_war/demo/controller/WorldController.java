package com.pomodoro_war.demo.controller;

import com.pomodoro_war.demo.services.WorldService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/world")
public class WorldController {

    private final WorldService worldService;

    //Para ir a la nueva zona desbloqueada
    @PostMapping("/travel")
    public ResponseEntity<String> travelToNextZone(Authentication auth) {
        try {
            worldService.travelToNextZone(auth.getName());
            return ResponseEntity.ok("Has viajado con éxito a la nueva zona. ¡Prepárate para el combate!");
        } catch (IllegalStateException e) {
            // Devuelve un 400 Bad Request si intenta viajar sin haber completado la zona
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //Retroceder
    @PostMapping("/retreat")
    public ResponseEntity<String> retreatFromCampaign(Authentication auth) {
        String message = worldService.retreatFromCampaign(auth.getName());
        return ResponseEntity.ok(message);
    }
}