package com.pomodoro_war.demo.services;

import com.pomodoro_war.demo.dtos.response.BestiaryResponse;
import com.pomodoro_war.demo.mappers.BestiaryMapper;
import com.pomodoro_war.demo.repositories.BestiaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BestiaryService {

    private final BestiaryRepository bestiaryRepository;
    private final BestiaryMapper bestiaryMapper;

    // Obtener todo el lore de golpe
    public List<BestiaryResponse> getAllEntries() {
        return bestiaryRepository.findAll().stream()
                .map(bestiaryMapper::toDto)
                .collect(Collectors.toList());
    }

    // Obtener filtrado por bando (Ej: team = "hero")
    public List<BestiaryResponse> getEntriesByTeam(String team) {
        return bestiaryRepository.findByTeam(team).stream()
                .map(bestiaryMapper::toDto)
                .collect(Collectors.toList());
    }
}