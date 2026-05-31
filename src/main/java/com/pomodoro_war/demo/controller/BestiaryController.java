package com.pomodoro_war.demo.controller;

import com.pomodoro_war.demo.dtos.response.BestiaryResponse;
import com.pomodoro_war.demo.services.BestiaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bestiario")
@RequiredArgsConstructor
public class BestiaryController {

    private final BestiaryService bestiaryService;

    // Ruta: GET /api/bestiario
    @GetMapping
    public ResponseEntity<List<BestiaryResponse>> getAllEntries() {
        return ResponseEntity.ok(bestiaryService.getAllEntries());
    }

    // Ruta: GET /api/bestiario/bando/{team}
    @GetMapping("/bando/{team}")
    public ResponseEntity<List<BestiaryResponse>> getEntriesByTeam(@PathVariable String team) {
        return ResponseEntity.ok(bestiaryService.getEntriesByTeam(team));
    }
}