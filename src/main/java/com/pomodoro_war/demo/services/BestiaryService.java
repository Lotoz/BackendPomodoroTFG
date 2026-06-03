package com.pomodoro_war.demo.services;

import com.pomodoro_war.demo.dtos.response.BestiaryResponse;
import com.pomodoro_war.demo.entities.enums.ZoneType;
import com.pomodoro_war.demo.entities.world.WorldProgress;
import com.pomodoro_war.demo.mappers.BestiaryMapper;
import com.pomodoro_war.demo.repositories.BestiaryRepository;
import com.pomodoro_war.demo.repositories.WorldProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BestiaryService {

    private final BestiaryRepository bestiaryRepository;
    private final BestiaryMapper bestiaryMapper;
    private final WorldProgressRepository worldProgressRepository;

    // Obtener todo el lore filtrado por las zonas descubiertas
    public List<BestiaryResponse> getAllUnlockedEntries(String username) {
        WorldProgress progress = worldProgressRepository.findByUserUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Progreso no encontrado"));

        List<String> unlockedZones = getUnlockedZones(progress.getCurrentZone());

        return bestiaryRepository.findAll().stream()
                .filter(entry -> unlockedZones.contains(entry.getZone().toUpperCase()))
                .map(bestiaryMapper::toDto)
                .collect(Collectors.toList());
    }

    // Obtener filtrado por bando y por las zonas descubiertas
    public List<BestiaryResponse> getUnlockedEntriesByTeam(String team, String username) {
        WorldProgress progress = worldProgressRepository.findByUserUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Progreso no encontrado"));

        List<String> unlockedZones = getUnlockedZones(progress.getCurrentZone());

        return bestiaryRepository.findByTeam(team).stream()
                .filter(entry -> unlockedZones.contains(entry.getZone().toUpperCase()))
                .map(bestiaryMapper::toDto)
                .collect(Collectors.toList());
    }

    // desbloqueo por zona
    private List<String> getUnlockedZones(ZoneType currentZone) {
        List<String> allowedZones = new ArrayList<>();

        // El Mundo 1 siempre está desbloqueado
        allowedZones.add("INITIAL");

        if (currentZone == ZoneType.FOREST || currentZone == ZoneType.LAVA || currentZone == ZoneType.INFINITE) {
            allowedZones.add("FOREST");
        }

        if (currentZone == ZoneType.LAVA || currentZone == ZoneType.INFINITE) {
            allowedZones.add("LAVA");
            allowedZones.add("INFINITE");
        }

        return allowedZones;
    }
}