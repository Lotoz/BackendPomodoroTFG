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

        List<ZoneType> unlockedZones = getUnlockedZones(progress.getCurrentZone());

        return bestiaryRepository.findAll().stream()
                .filter(entry -> unlockedZones.contains(entry.getZone()))
                .map(bestiaryMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<BestiaryResponse> getUnlockedEntriesByTeam(String team, String username) {
        WorldProgress progress = worldProgressRepository.findByUserUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Progreso no encontrado"));

        List<ZoneType> unlockedZones = getUnlockedZones(progress.getCurrentZone());

        return bestiaryRepository.findByTeam(team).stream()
                .filter(entry -> unlockedZones.contains(entry.getZone()))
                .map(bestiaryMapper::toDto)
                .collect(Collectors.toList());
    }

    private List<ZoneType> getUnlockedZones(ZoneType currentZone) {
        List<ZoneType> allowedZones = new ArrayList<>();

        allowedZones.add(ZoneType.INITIAL);

        if (currentZone == ZoneType.FOREST || currentZone == ZoneType.LAVA || currentZone == ZoneType.INFINITE) {
            allowedZones.add(ZoneType.FOREST);
        }

        if (currentZone == ZoneType.LAVA || currentZone == ZoneType.INFINITE) {
            allowedZones.add(ZoneType.LAVA);
            allowedZones.add(ZoneType.INFINITE);
        }

        return allowedZones;
    }
}