package com.pomodoro_war.demo.services;

import com.pomodoro_war.demo.entities.auth.User;
import com.pomodoro_war.demo.entities.enums.ZoneType;
import com.pomodoro_war.demo.entities.person.Beast;
import com.pomodoro_war.demo.entities.person.Hero;
import com.pomodoro_war.demo.entities.person.beasts.Goblins;
import com.pomodoro_war.demo.entities.person.beasts.Naga;
import com.pomodoro_war.demo.entities.person.beasts.Orcus;
import com.pomodoro_war.demo.entities.person.beasts.Sorcerer;
import com.pomodoro_war.demo.entities.person.heroes.Cleric;
import com.pomodoro_war.demo.entities.person.heroes.Wizard;
import com.pomodoro_war.demo.entities.world.WorldProgress;

import com.pomodoro_war.demo.repositories.PersonRepository;
import com.pomodoro_war.demo.repositories.WorldProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class WorldService {

    private final PersonRepository personRepository;
    private final Random random = new Random();
    private final WorldProgressRepository worldProgressRepository;

    @Transactional
    public List<Beast> ensureActiveBeasts(String username) {
        List<Beast> currentBeasts = personRepository.findActiveBeastsByUsername(username);

        if (!currentBeasts.isEmpty()) {
            return currentBeasts;
        }

        WorldProgress progress = worldProgressRepository.findByUserUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Progreso no encontrado"));

        int stage = progress.getCurrentStage();
        int amountOfEnemies;

        if (stage > 5) {
            amountOfEnemies = 4;
        } else if (stage >= 4) {
            amountOfEnemies = 3;
        } else if (stage >= 2) {
            amountOfEnemies = 2;
        } else {
            amountOfEnemies = 1;
        }

        return generateBeastWave(progress, amountOfEnemies);
    }

    @Transactional
    public List<Beast> generateBeastWave(WorldProgress progress, int amount) {
        List<Beast> newWave = new ArrayList<>();
        User user = progress.getUser();
        int stage = progress.getCurrentStage();
        ZoneType currentZone = progress.getCurrentZone();

        boolean isBossStage = (stage % 10 == 0);

        // LISTA DE APODOS TEMIBLES (Barajamos para que sean únicos en cada oleada)
        List<String> apodos = new ArrayList<>(List.of(
                "el Cruel", "el Sanguinario", "Ojo de Sangre", "Garra Rota",
                "el Implacable", "el Sombrío", "el Despiadado", "Piel de Hierro",
                "el Carnicero", "el Voraz", "el Salvaje", "el Rompehuesos",
                "Cicatriz", "el Traicionero", "el Loco"
        ));
        Collections.shuffle(apodos, random);

        if (isBossStage) {
            Beast boss = createBossBeast(currentZone, stage, user);
            newWave.add(boss);

            List<String> allowedBeasts = getAllowedBeastsForZone(currentZone);
            for (int i = 0; i < 2; i++) {
                String chosenType = allowedBeasts.get(random.nextInt(allowedBeasts.size()));
                String apodo = apodos.get(i);

                Beast esbirro = createScaledBeast(chosenType, stage, currentZone, user, apodo);
                newWave.add(esbirro);
            }
        } else {
            List<String> allowedBeasts = getAllowedBeastsForZone(currentZone);
            for (int i = 0; i < amount; i++) {
                String chosenType = allowedBeasts.get(random.nextInt(allowedBeasts.size()));
                String apodo = apodos.get(i);

                Beast beast = createScaledBeast(chosenType, stage, currentZone, user, apodo);
                newWave.add(beast);
            }
        }

        return personRepository.saveAll(newWave);
    }

    @Transactional
    public void travelToNextZone(String username) {
        WorldProgress progress = worldProgressRepository.findByUserUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Progreso no encontrado"));

        if (progress.getCurrentStage() < 10 && progress.getCurrentZone() != ZoneType.INFINITE) {
            throw new IllegalStateException("Aún no has limpiado esta zona por completo.");
        }

        switch (progress.getCurrentZone()) {
            case INITIAL:
                progress.setCurrentZone(ZoneType.FOREST);
                break;
            case FOREST:
                progress.setCurrentZone(ZoneType.LAVA);
                break;
            case LAVA:
                progress.setCurrentZone(ZoneType.INFINITE);
                break;
            case INFINITE:
                throw new IllegalStateException("Ya estás en la zona final del juego.");
        }

        progress.setCurrentStage(1);
        unlockHeroesForNewZone(progress);
        worldProgressRepository.save(progress);

        generateBeastWave(progress, 3);
    }

    @Transactional
    public void unlockHeroesForNewZone(WorldProgress progress) {
        User user = progress.getUser();
        List<Hero> newUnlockedHeroes = new ArrayList<>();

        if (progress.getCurrentZone() == ZoneType.FOREST && progress.getCurrentStage() == 1) {
            Cleric cleric = new Cleric("Clérigo Novato", 80, 5);
            cleric.setUser(user);
            newUnlockedHeroes.add(cleric);
        }
        else if (progress.getCurrentZone() == ZoneType.LAVA && progress.getCurrentStage() == 1) {
            Wizard wizard = new Wizard("Mago Arcano", 70, 3);
            wizard.setUser(user);
            newUnlockedHeroes.add(wizard);
        }

        if (!newUnlockedHeroes.isEmpty()) {
            personRepository.saveAll(newUnlockedHeroes);
        }
    }

    @Transactional
    public String retreatFromCampaign(String username) {
        WorldProgress progress = worldProgressRepository.findByUserUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Progreso no encontrado"));

        progress.setCurrentStage(1);
        worldProgressRepository.save(progress);

        List<Beast> activeBeasts = personRepository.findActiveBeastsByUsername(username);
        personRepository.deleteAll(activeBeasts);

        return "Has tocado la campana de retirada. Tus héroes supervivientes han huido al campamento.";
    }

    // --- MÉTODOS AUXILIARES ---

    private List<String> getAllowedBeastsForZone(ZoneType zone) {
        List<String> allowedBeasts = new ArrayList<>();
        switch (zone) {
            case INITIAL:
                allowedBeasts.addAll(List.of("GOBLIN", "ORCUS"));
                break;
            case FOREST:
                allowedBeasts.addAll(List.of("GOBLIN", "ORCUS", "NAGA"));
                break;
            case LAVA:
            case INFINITE:
                allowedBeasts.addAll(List.of("GOBLIN", "ORCUS", "NAGA", "SORCERER"));
                break;
        }
        return allowedBeasts;
    }

    private Beast createBossBeast(ZoneType zone, int stage, User user) {
        int zoneMultiplier = switch (zone) {
            case INITIAL -> 0;
            case FOREST -> 1;
            case LAVA -> 2;
            case INFINITE -> 4;
        };

        int effectiveLevel = stage + (zoneMultiplier * 10);
        int bossLife = 200 + (effectiveLevel * 30);
        int bossArmor = 15 + (effectiveLevel * 4);

        Beast boss = null;
        String displayName = " [JEFE] Nv." + effectiveLevel;

        switch (zone) {
            case INITIAL:
                boss = new Orcus("Rey Orco" + displayName, bossLife, bossArmor);
                break;
            case FOREST:
                boss = new Naga("Reina Naga" + displayName, bossLife, bossArmor);
                break;
            case LAVA:
            case INFINITE:
                boss = new Sorcerer("Señor Oscuro" + displayName, bossLife + 100, bossArmor + 5);
                break;
        }

        if(boss != null) boss.setUser(user);
        return boss;
    }

    // MODIFICADO: Ahora recibe el apodo y lo inyecta en el nombre
    private Beast createScaledBeast(String type, int stage, ZoneType zone, User user, String apodo) {
        int zoneMultiplier = switch (zone) {
            case INITIAL -> 0;
            case FOREST -> 1;
            case LAVA -> 2;
            case INFINITE -> 4;
        };

        int effectiveLevel = stage + (zoneMultiplier * 10);
        int bonusLife = effectiveLevel * 10;
        int bonusArmor = effectiveLevel * 2;

        Beast beast = null;
        // Construimos el nombre visual: " 'el Cruel' Nv.12"
        String displayName = " '" + apodo + "' Nv." + effectiveLevel;

        switch (type) {
            case "GOBLIN":
                beast = new Goblins("Duende" + displayName, 40 + bonusLife, 2 + bonusArmor);
                break;
            case "ORCUS":
                beast = new Orcus("Orco" + displayName, 90 + bonusLife, 10 + bonusArmor);
                break;
            case "NAGA":
                beast = new Naga("Naga" + displayName, 70 + bonusLife, 5 + bonusArmor);
                break;
            case "SORCERER":
                beast = new Sorcerer("Brujo Oscuro" + displayName, 60 + bonusLife, 4 + bonusArmor);
                break;
        }

        if (beast != null) beast.setUser(user);
        return beast;
    }
}