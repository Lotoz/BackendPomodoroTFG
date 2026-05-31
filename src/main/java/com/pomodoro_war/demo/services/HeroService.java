package com.pomodoro_war.demo.services;

import com.pomodoro_war.demo.entities.person.Hero;
import com.pomodoro_war.demo.repositories.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HeroService {

    private final PersonRepository personRepository;

    public List<Hero> getAllAliveHeroes(String username) {
        return personRepository.findAllAliveHeroesByUsername(username);
    }

    @Transactional
    public String toggleHeroActiveStatus(String username, Long heroId) {
        Hero hero = (Hero) personRepository.findById(heroId)
                .orElseThrow(() -> new IllegalArgumentException("Héroe no encontrado"));

        if (!hero.getUser().getUsername().equals(username)) {
            throw new IllegalStateException("Ese héroe no te pertenece.");
        }

        // Si el héroe está en el banquillo y queremos activarlo, comprobamos el límite
        if (!hero.isInActivateTeam()) {
            int currentActiveTeamSize = personRepository.countActiveTeamHeroesByUsername(username);

            if (currentActiveTeamSize >= 4) {
                throw new IllegalStateException("¡Tu equipo de vanguardia está lleno! Máximo 4 héroes. Manda a alguien al banquillo primero.");
            }

            hero.setInActivateTeam(true);
        } else {
            // Si ya estaba activo, simplemente lo mandamos al banquillo (no hay límite para desactivar)
            hero.setInActivateTeam(false);
        }

        personRepository.save(hero);

        return hero.isInActivateTeam() ?
                hero.getName() + " se ha unido al equipo de batalla." :
                hero.getName() + " descansará en el campamento.";
    }
}