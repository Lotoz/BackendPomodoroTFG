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
        ZoneType currentZone = worldProgressRepository.findByUserUsername(username)
                .map(WorldProgress::getCurrentZone)
                .orElse(ZoneType.INITIAL);

        List<ZoneType> unlockedZones = getUnlockedZones(currentZone);

        return bestiaryRepository.findAll().stream()
                // Comparación estricta y limpia con Enums
                .filter(entry -> entry.getZone() != null && unlockedZones.contains(entry.getZone()))
                .map(bestiaryMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<BestiaryResponse> getUnlockedEntriesByTeam(String team, String username) {
        ZoneType currentZone = worldProgressRepository.findByUserUsername(username)
                .map(WorldProgress::getCurrentZone)
                .orElse(ZoneType.INITIAL);

        List<ZoneType> unlockedZones = getUnlockedZones(currentZone);

        return bestiaryRepository.findByTeam(team).stream()
                .filter(entry -> entry.getZone() != null && unlockedZones.contains(entry.getZone()))
                .map(bestiaryMapper::toDto)
                .collect(Collectors.toList());
    }

    private List<ZoneType> getUnlockedZones(ZoneType currentZone) {
        List<ZoneType> allowedZones = new ArrayList<>();

        allowedZones.add(ZoneType.INITIAL);

        if (currentZone != null) {
            if (currentZone == ZoneType.FOREST || currentZone == ZoneType.LAVA || currentZone == ZoneType.INFINITE) {
                allowedZones.add(ZoneType.FOREST);
            }

            if (currentZone == ZoneType.LAVA || currentZone == ZoneType.INFINITE) {
                allowedZones.add(ZoneType.LAVA);
                allowedZones.add(ZoneType.INFINITE);
            }
        }

        return allowedZones;
    }
}