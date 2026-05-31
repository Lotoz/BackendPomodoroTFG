package com.pomodoro_war.demo.controller;

import com.pomodoro_war.demo.dtos.response.TavernHeroDto;
import com.pomodoro_war.demo.services.TavernService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tavern")
public class TavernController {

    private final TavernService tavernService;

    @GetMapping("/offers")
    public ResponseEntity<List<TavernHeroDto>> getDailyOffers(Authentication auth) {
        List<TavernHeroDto> offers = tavernService.getDailyTavernOffers(auth.getName())
                .stream()
                .map(TavernHeroDto::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(offers);
    }
    
    @PostMapping("/recruit")
    public ResponseEntity<String> recruitHero(@RequestParam String heroName, Authentication auth) {
        try {
            String message = tavernService.recruitHero(auth.getName(), heroName);
            return ResponseEntity.ok(message);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/dismiss/{heroId}")
    public ResponseEntity<String> dismissHero(@PathVariable Long heroId, Authentication auth) {
        try {
            tavernService.dismissHero(auth.getName(), heroId);
            return ResponseEntity.ok("El héroe ha sido despedido y abandona tu campamento.");
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}