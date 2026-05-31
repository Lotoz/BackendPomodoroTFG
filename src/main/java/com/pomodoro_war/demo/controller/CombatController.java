package com.pomodoro_war.demo.controller;

import com.pomodoro_war.demo.dtos.request.HeroActionRequest;
import com.pomodoro_war.demo.dtos.response.CombatStateResponse;
import com.pomodoro_war.demo.dtos.response.FallenHeroResponse;
import com.pomodoro_war.demo.repositories.FallenHeroRepository;
import com.pomodoro_war.demo.services.CombatService;
import com.pomodoro_war.demo.services.WorldService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/combat")
public class CombatController {

    private final CombatService combatService;
    private final WorldService worldService;
    private final FallenHeroRepository fallenHeroRepository;

    @PostMapping("/execute-round")
    public ResponseEntity<CombatStateResponse> executeRound(
            @RequestBody List<HeroActionRequest> actions,
            Authentication auth) {
        CombatStateResponse response = combatService.executeFullRound(actions, auth.getName());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/state")
    public ResponseEntity<?> getCombatState(Authentication auth) {
        try {
            worldService.ensureActiveBeasts(auth.getName());
            CombatStateResponse response = combatService.executeFullRound(java.util.Collections.emptyList(), auth.getName());
            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            // Si el jugador entra sin equipo, devolvemos un 400 Bad Request
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/cementerio")
    public ResponseEntity<List<FallenHeroResponse>> getGraveyard(Authentication auth) {
        List<FallenHeroResponse> fallenHeroes = fallenHeroRepository.findByUserUsernameOrderByFallenAtDesc(auth.getName())
                .stream()
                .map(FallenHeroResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(fallenHeroes);
    }
}