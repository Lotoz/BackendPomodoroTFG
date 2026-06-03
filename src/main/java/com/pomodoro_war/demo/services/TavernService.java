package com.pomodoro_war.demo.services;

import com.pomodoro_war.demo.entities.auth.User;
import com.pomodoro_war.demo.entities.enums.ZoneType;
import com.pomodoro_war.demo.entities.person.Hero;
import com.pomodoro_war.demo.entities.person.heroes.*;
import com.pomodoro_war.demo.entities.world.WorldProgress;

import com.pomodoro_war.demo.repositories.FallenHeroRepository;
import com.pomodoro_war.demo.repositories.PersonRepository;
import com.pomodoro_war.demo.repositories.UserRepository;
import com.pomodoro_war.demo.repositories.WorldProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class TavernService {

    private final PersonRepository personRepository;
    private final WorldProgressRepository worldProgressRepository;
    private final UserRepository userRepository;
    private final FallenHeroRepository fallenHeroRepository;

    /**
     * Calcula el límite máximo de héroes según la zona actual.
     */
    public int getMaxHeroesAllowed(ZoneType zone) {
        switch (zone) {
            case INITIAL: return 10;
            case FOREST: return 12;
            case LAVA: return 14;
            case INFINITE: return 16;
            default: return 10;
        }
    }

    /**
     * Devuelve los héroes disponibles HOY en la Taberna para este usuario.
     * FILTRA los héroes que ya han sido contratados (vivos o muertos) sin rellenar infinitamente.
     */
    public List<Hero> getDailyTavernOffers(String username) {
        WorldProgress progress = worldProgressRepository.findByUserUsername(username)
                .orElseGet(() -> {
                    User user = userRepository.findById(username).orElseThrow();
                    WorldProgress newProgress = new WorldProgress();
                    newProgress.setUser(user);
                    newProgress.setCurrentZone(ZoneType.INITIAL);
                    newProgress.setCurrentStage(1);
                    return worldProgressRepository.save(newProgress);
                });

        //  Semilla GLOBAL basada en el DÍA y en la ZONA del jugador.
        // Así todos los jugadores en el mismo mundo ven las mismas ofertas iniciales.
        long seed = LocalDate.now().toEpochDay() + progress.getCurrentZone().name().hashCode();
        Random dailyRandom = new Random(seed);

        List<String> allowedClasses = getAllowedHeroClasses(progress.getCurrentZone());

        // Generamos SIEMPRE los 4 héroes fijos del servidor para este día
        List<Hero> globalDailyOffers = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            String heroClass = allowedClasses.get(dailyRandom.nextInt(allowedClasses.size()));
            globalDailyOffers.add(generateHeroTemplate(heroClass, i));
        }

        // Obtenemos el historial personal de este jugador concreto
        List<String> hiredHeroNames = personRepository.findAllAliveHeroesByUsername(username)
                .stream().map(Hero::getName).toList();
        List<String> deadHeroNames = fallenHeroRepository.findByUserUsernameOrderByFallenAtDesc(username)
                .stream().map(fh -> fh.getName()).toList();

        // Filtramos la lista global: Quitamos los que el jugador ya tiene en su ejército o cementerio
        return globalDailyOffers.stream()
                .filter(h -> !hiredHeroNames.contains(h.getName()) && !deadHeroNames.contains(h.getName()))
                .toList();
    }

    @Transactional
    public String recruitHero(String username, String heroName) {
        WorldProgress progress = worldProgressRepository.findByUserUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Progreso no encontrado"));

        List<Hero> currentCamp = personRepository.findAllAliveHeroesByUsername(username);
        int maxAllowed = getMaxHeroesAllowed(progress.getCurrentZone());

        if (currentCamp.size() >= maxAllowed) {
            throw new IllegalStateException("¡Tu campamento está lleno! Límite de " + maxAllowed + " héroes. Debes despedir a alguien antes de reclutar.");
        }

        if (currentCamp.stream().anyMatch(h -> h.getName().equals(heroName))) {
            throw new IllegalStateException("Ya has reclutado a este héroe hoy.");
        }

        List<Hero> offers = getDailyTavernOffers(username);

        Hero chosenHero = offers.stream()
                .filter(h -> h.getName().equals(heroName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Ese héroe no está en la taberna hoy."));

        User user = userRepository.findById(username).orElseThrow();
        chosenHero.setUser(user);
        chosenHero.setInActivateTeam(false);

        personRepository.save(chosenHero);

        return "¡" + chosenHero.getName() + " se ha unido a tu campamento!";
    }

    @Transactional
    public void dismissHero(String username, Long heroId) {
        Hero hero = (Hero) personRepository.findById(heroId)
                .orElseThrow(() -> new IllegalArgumentException("Héroe no encontrado"));

        if (!hero.getUser().getUsername().equals(username)) {
            throw new IllegalStateException("No puedes despedir a un héroe que no es tuyo.");
        }

        personRepository.delete(hero);
    }

    // --- MÉTODOS AUXILIARES PRIVADOS ---

    private List<String> getAllowedHeroClasses(ZoneType currentZone) {
        List<String> classes = new ArrayList<>(List.of("Warrior", "Dwarf", "Elf"));

        if (currentZone == ZoneType.FOREST || currentZone == ZoneType.LAVA || currentZone == ZoneType.INFINITE) {
            classes.add("Cleric");
        }
        if (currentZone == ZoneType.LAVA || currentZone == ZoneType.INFINITE) {
            classes.add("Wizard");
        }
        return classes;
    }

    private Hero generateHeroTemplate(String type, int index) {
        String[] prefixes = {"Valiente", "Fiero", "Sabio", "Errante", "Misterioso", "Joven", "Viejo", "Renegado", "Exiliado", "Fugitivo"};
        String title = prefixes[index % prefixes.length];

        switch (type) {
            case "Warrior": return new Warrior("Caballero " + title, 100, 10);
            case "Dwarf": return new Dwarf("Enano " + title, 120, 8);
            case "Elf": return new Elf("Elfo " + title, 80, 5);
            case "Cleric": return new Cleric("Clérigo " + title, 70, 4);
            case "Wizard": return new Wizard("Mago " + title, 60, 2);
            default: return new Warrior("Recluta", 90, 5);
        }
    }
}