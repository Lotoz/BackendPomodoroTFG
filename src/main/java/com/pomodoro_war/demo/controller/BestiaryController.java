package com.pomodoro_war.demo.controller;

import com.pomodoro_war.demo.dtos.response.BestiaryResponse;
import com.pomodoro_war.demo.services.BestiaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bestiary")
@RequiredArgsConstructor
public class BestiaryController {

    private final BestiaryService bestiaryService;

    @GetMapping
    public ResponseEntity<List<BestiaryResponse>> getAllEntries(Authentication auth) {
        return ResponseEntity.ok(bestiaryService.getAllUnlockedEntries(auth.getName()));
    }

    @GetMapping("/team/{team}")
    public ResponseEntity<List<BestiaryResponse>> getEntriesByTeam(
            @PathVariable String team,
            Authentication auth) {
        return ResponseEntity.ok(bestiaryService.getUnlockedEntriesByTeam(team, auth.getName()));
    }
}