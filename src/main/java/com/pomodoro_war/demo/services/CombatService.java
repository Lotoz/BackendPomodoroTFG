package com.pomodoro_war.demo.services;

import com.pomodoro_war.demo.dtos.request.HeroActionRequest;
import com.pomodoro_war.demo.dtos.response.CombatStateResponse;
import com.pomodoro_war.demo.dtos.response.FighterStateDto;
import com.pomodoro_war.demo.entities.enums.CombatStatus;
import com.pomodoro_war.demo.entities.enums.ZoneType;
import com.pomodoro_war.demo.entities.lore.FallenHero;
import com.pomodoro_war.demo.entities.person.*;
import com.pomodoro_war.demo.entities.person.heroes.*;
import com.pomodoro_war.demo.entities.person.interfaces.Magic;
import com.pomodoro_war.demo.entities.world.WorldProgress;
import com.pomodoro_war.demo.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CombatService {

    private final PersonRepository personRepository;
    private final FallenHeroRepository fallenHeroRepository;
    private final WorldProgressRepository worldProgressRepository;
    private final UserRepository userRepository;

    @Transactional
    public CombatStateResponse executeFullRound(List<HeroActionRequest> heroActions, String username) {
        List<String> logs = new ArrayList<>();

        WorldProgress progress = worldProgressRepository.findByUserUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Progreso no encontrado"));

        List<Hero> activeHeroes = personRepository.findActiveHeroesByUsername(username);

        if (activeHeroes.isEmpty() && heroActions.isEmpty()) {
            throw new IllegalStateException("Tu equipo activo está vacío. ¡Ve al Campamento y prepara a tus héroes antes de luchar!");
        }

        List<Beast> activeBeasts = personRepository.findActiveBeastsByUsername(username);

        if (heroActions.isEmpty()) {
            return buildResponse(CombatStatus.ONGOING, progress, activeHeroes, activeBeasts, logs);
        }

        Map<Long, Person> combatants = new HashMap<>();
        activeHeroes.forEach(h -> combatants.put(h.getId(), h));
        activeBeasts.forEach(b -> combatants.put(b.getId(), b));

        for (HeroActionRequest action : heroActions) {
            Hero hero = (Hero) combatants.get(action.getHeroId());

            // Si el héroe murió en esta misma ronda antes de su turno, no actúa
            if (hero == null || !hero.isState()) continue;

            long currentBeastsAlive = activeBeasts.stream().filter(Person::isState).count();
            if (!"HEAL".equals(action.getActionType()) && currentBeastsAlive == 0) {
                break;
            }

            Person target = combatants.get(action.getTargetId());

            if (target == null || !target.isState()) {
                if ("HEAL".equals(action.getActionType())) {
                    target = activeHeroes.stream()
                            .filter(h -> h.isState() && h.getLife() < h.getLifeMax())
                            .findFirst()
                            .orElse(activeHeroes.stream().filter(Person::isState).findFirst().orElse(null));
                } else {
                    // Selecciona reactivamente la primera bestia que SÍ esté viva en este milisegundo
                    target = activeBeasts.stream().filter(Person::isState).findFirst().orElse(null);
                }
            }

            // Si tras el auto-apuntado sigue sin haber un objetivo válido vivo, saltamos la acción
            if (target == null || !target.isState()) {
                continue;
            }

            if (applyStatusEffects(hero, progress, username, logs)) continue;
            if (checkExhaustion(hero, logs)) continue;

            if ("HEAL".equals(action.getActionType()) && hero instanceof Cleric cleric) {
                cleric.healTarget(target);
                logs.add(hero.getName() + " curó a " + target.getName() + " por 30 de vida.");
            } else {
                if (hero instanceof Magic magicHero && Math.random() > 0.5) {
                    magicHero.applicationStun(target);
                    logs.add(hero.getName() + " aturdió a " + target.getName() + " con magia.");
                } else {
                    executeAttack(hero, target, progress, username, logs);
                }
            }
        }

        long beastsAliveAfterHeroes = activeBeasts.stream().filter(Person::isState).count();
        if (beastsAliveAfterHeroes == 0) {
            // Limpieza física requerida para la persistencia antes de cantar victoria
            activeBeasts.removeIf(beast -> !beast.isState());
            return handleVictory(progress, activeHeroes, logs);
        }

        for (Beast beast : activeBeasts) {
            // COMPROBACIÓN REPRODUCTIVA: Si murió por ataques previos o veneno en su propio mantenimiento
            if (!beast.isState()) continue;

            // Comprobamos si quedan héroes en pie antes de que este monstruo específico alce su arma
            long heroesAlive = activeHeroes.stream().filter(Person::isState).count();
            if (heroesAlive == 0) break;

            if (applyStatusEffects(beast, progress, username, logs)) continue;
            if (checkExhaustion(beast, logs)) continue;

            Hero target = selectIntelligentTarget(activeHeroes);
            if (target != null && target.isState()) { // Validamos que el objetivo inteligente siga vivo
                if (beast instanceof Magic magicBeast && Math.random() > 0.6) {
                    magicBeast.applicationStun(target);
                    logs.add(beast.getName() + " aturdió a " + target.getName() + ".");
                } else {
                    executeAttack(beast, target, progress, username, logs);
                }
            }
        }

        personRepository.saveAll(activeHeroes);
        personRepository.saveAll(activeBeasts);

        activeBeasts.removeIf(beast -> !beast.isState());
        activeHeroes.removeIf(hero -> !hero.isState());

        if (activeHeroes.isEmpty()) {
            return handleDefeat(progress, activeBeasts, logs);
        }

        return buildResponse(CombatStatus.ONGOING, progress, activeHeroes, activeBeasts, logs);
    }

    // --- MÉTODOS AUXILIARES ---

    private void executeAttack(Person attacker, Person target, WorldProgress progress, String username, List<String> logs) {
        int damage = attacker.attack(target);
        boolean isCritical = Math.random() > 0.8;

        if (isCritical) {
            damage = (int) (damage * 1.5);
            target.receiveDamage(damage);
            logs.add("¡GOLPE CRÍTICO! " + attacker.getName() + " atacó a " + target.getName() + " por " + damage + " de daño.");
        } else {
            target.receiveDamage(damage);
            logs.add(attacker.getName() + " atacó a " + target.getName() + " por " + damage + " de daño.");
        }

        checkDeath(target, attacker, progress, username, logs);
    }

    private boolean checkExhaustion(Person person, List<String> logs) {
        if (person.getLife() <= (person.getLifeMax() * 0.3)) {
            if (Math.random() > 0.5) {
                logs.add(person.getName() + " está demasiado exhausto por sus heridas para moverse.");
                return true;
            }
        }
        return false;
    }

    private boolean applyStatusEffects(Person person, WorldProgress progress, String username, List<String> logs) {
        if (person.isPoisoned()) {
            if (!(person instanceof Elf)) {
                person.receiveDamage(5);
                logs.add(person.getName() + " sufre 5 de daño por veneno.");
                // Si muere por veneno, lo procesamos de inmediato
                checkDeath(person, null, progress, username, logs);
            } else {
                logs.add("El veneno corroe la armadura de " + person.getName() + ", pero su sangre feérica evita el daño.");
            }
            person.setPoisonTurns(person.getPoisonTurns() - 1);
            if (person.getPoisonTurns() <= 0) person.setPoisoned(false);
        }

        // Si el veneno lo mató, abortamos su turno
        if (!person.isState()) return true;

        if (person.isStun()) {
            if (person.getTimeStun() < 1) {
                person.setTimeStun(person.getTimeStun() + 1);
                logs.add(person.getName() + " está aturdido y no puede atacar.");
                return true;
            } else {
                person.setTimeStun(0);
                person.setStun(false);
            }
        }
        return false;
    }

    private Hero selectIntelligentTarget(List<Hero> teamEnemy) {
        for (Hero hero : teamEnemy) {
            if (hero.isStun() && hero.isState()) return hero;
        }
        for (Hero hero : teamEnemy) {
            if (hero.getArmor() < (hero.getLife() * 0.20) && hero.isState()) return hero;
        }
        return teamEnemy.stream().filter(Person::isState).findFirst().orElse(null);
    }

    private void checkDeath(Person target, Person killer, WorldProgress progress, String username, List<String> logs) {
        if (target.getLife() <= 0 && target.isState()) {
            target.setState(false); // Borrado lógico (el héroe desaparece del campamento)

            if (target instanceof Hero hero) {
                String killerName = (killer != null) ? killer.getName() : "el veneno o sus heridas";
                String reason = String.format("Cayó defendiendo el reino. Abatido por %s en la etapa %d de %s.",
                        killerName, progress.getCurrentStage(), progress.getCurrentZone().name());

                fallenHeroRepository.buryHeroNative(
                        hero.getName(),
                        hero.getClass().getSimpleName(),
                        hero.getLevel(),
                        reason,
                        LocalDateTime.now(),
                        username
                );

                logs.add("¡" + hero.getName() + " ha muerto! Su alma descansa en el Cementerio.");
            } else {
                logs.add(target.getName() + " ha sido destruido.");
            }
        }
    }

    private CombatStateResponse handleVictory(WorldProgress progress, List<Hero> heroes, List<String> logs) {
        logs.add("¡Has limpiado esta etapa!");
        CombatStatus status = CombatStatus.VICTORY;
        if (progress.getCurrentZone() == ZoneType.INFINITE) {
            progress.setCurrentStage(progress.getCurrentStage() + 1);
            logs.add("Te adentras más profundo en la oscuridad... Avanzas a la etapa " + progress.getCurrentStage());
        } else if (progress.getCurrentStage() >= 10) {
            status = CombatStatus.ZONE_CLEARED;
            logs.add("¡Zona completada! El camino hacia la siguiente región está despejado.");
        } else {
            progress.setCurrentStage(progress.getCurrentStage() + 1);
        }
        worldProgressRepository.save(progress);
        personRepository.saveAll(heroes);
        return buildResponse(status, progress, heroes, new ArrayList<>(), logs);
    }

    private CombatStateResponse handleDefeat(WorldProgress progress, List<Beast> beasts, List<String> logs) {
        logs.add("Todo tu equipo ha sido aniquilado. La oscuridad avanza...");
        personRepository.saveAll(beasts);
        return buildResponse(CombatStatus.DEFEAT, progress, new ArrayList<>(), beasts, logs);
    }

    private CombatStateResponse buildResponse(CombatStatus status, WorldProgress progress,
                                              List<Hero> heroes, List<Beast> beasts, List<String> logs) {

        List<FighterStateDto> heroDtos = heroes.stream()
                .map(h -> new FighterStateDto(
                        h.getId(), h.getName(), "hero", h.getClass().getSimpleName(),
                        h.getLife(), h.getLifeMax(), h.getArmor(), h.isStun(), h.isPoisoned(), h.isState()))
                .collect(Collectors.toList());

        List<FighterStateDto> beastDtos = beasts.stream()
                .map(b -> new FighterStateDto(
                        b.getId(), b.getName(), "beast", b.getClass().getSimpleName(),
                        b.getLife(), b.getLifeMax(), b.getArmor(), b.isStun(), b.isPoisoned(), b.isState()))
                .collect(Collectors.toList());

        return new CombatStateResponse(status, progress.getCurrentZone(), progress.getCurrentStage(), heroDtos, beastDtos, logs);
    }
}