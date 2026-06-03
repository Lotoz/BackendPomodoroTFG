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

    public List<BestiaryResponse> getAllUnlockedEntries(String username) {
        WorldProgress progress = worldProgressRepository.findByUserUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Progreso no encontrado"));

        List<String> unlockedZones = getUnlockedZonesStrings(progress.getCurrentZone());

        return bestiaryRepository.findAll().stream()
                // Convertimos el dato de la base a String seguro para compararlo
                .filter(entry -> unlockedZones.contains(entry.getZone().toString().toUpperCase()))
                .map(bestiaryMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<BestiaryResponse> getUnlockedEntriesByTeam(String team, String username) {
        WorldProgress progress = worldProgressRepository.findByUserUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Progreso no encontrado"));

        List<String> unlockedZones = getUnlockedZonesStrings(progress.getCurrentZone());

        return bestiaryRepository.findByTeam(team).stream()
                // Convertimos el dato de la base a String seguro para compararlo
                .filter(entry -> unlockedZones.contains(entry.getZone().toString().toUpperCase()))
                .map(bestiaryMapper::toDto)
                .collect(Collectors.toList());
    }

    // Usamos Strings en lugar de Enums puristas para que no falle al leer la DB antigua
    private List<String> getUnlockedZonesStrings(ZoneType currentZone) {
        List<String> allowedZones = new ArrayList<>();

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