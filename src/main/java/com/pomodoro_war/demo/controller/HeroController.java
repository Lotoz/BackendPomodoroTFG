package com.pomodoro_war.demo.controller;

import com.pomodoro_war.demo.dtos.response.HeroCampDto;
import com.pomodoro_war.demo.repositories.PersonRepository;
import com.pomodoro_war.demo.services.HeroService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/heroes")
@RequiredArgsConstructor
public class HeroController {

    private final PersonRepository personRepository;
    private final HeroService heroService;

    // Obtiene SOLO a los héroes titulares (para el Combate y el Dashboard)
    @GetMapping("/activos")
    public ResponseEntity<List<HeroCampDto>> getActiveHeroes(Authentication auth) {
        List<HeroCampDto> activeHeroes = personRepository.findActiveHeroesByUsername(auth.getName())
                .stream()
                .map(HeroCampDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(activeHeroes);
    }

    // Obtiene a TODOS los héroes vivos (para la nueva vista del Campamento)
    @GetMapping("/todos")
    public ResponseEntity<List<HeroCampDto>> getAllAliveHeroes(Authentication auth) {
        List<HeroCampDto> allHeroes = heroService.getAllAliveHeroes(auth.getName())
                .stream()
                .map(HeroCampDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(allHeroes);
    }

    // Activa o desactiva a un héroe
    @PostMapping("/toggle-active/{id}")
    public ResponseEntity<String> toggleHeroStatus(@PathVariable Long id, Authentication auth) {
        try {
            String result = heroService.toggleHeroActiveStatus(auth.getName(), id);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}