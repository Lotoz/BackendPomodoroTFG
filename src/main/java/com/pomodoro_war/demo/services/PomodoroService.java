package com.pomodoro_war.demo.services;

import com.pomodoro_war.demo.dtos.request.PomodoroSettingsRequest;
import com.pomodoro_war.demo.dtos.request.PomodoroStateRequest;
import com.pomodoro_war.demo.dtos.response.FinishResponse;
import com.pomodoro_war.demo.dtos.response.PomodoroConfigResponse;
import com.pomodoro_war.demo.entities.auth.User;
import com.pomodoro_war.demo.entities.person.Hero;
import com.pomodoro_war.demo.entities.pomodoro.PomodoroConfiguration;
import com.pomodoro_war.demo.mappers.PomodoroMapper;
import com.pomodoro_war.demo.repositories.PersonRepository;
import com.pomodoro_war.demo.repositories.PomodoroConfigurationRepository;
import com.pomodoro_war.demo.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PomodoroService {

    private final PomodoroConfigurationRepository pomodoroRepository;
    private final PersonRepository personRepository;
    private final UserRepository userRepository;
    private final PomodoroMapper pomodoroMapper;

    // Método privado auxiliar para no repetir la lógica de buscar/crear entidad
    private PomodoroConfiguration getOrCreateConfigEntity(String username) {
        return pomodoroRepository.findByUserUsername(username).orElseGet(() -> {
            User user = userRepository.findById(username)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

            PomodoroConfiguration newConfig = new PomodoroConfiguration();
            newConfig.setUser(user);
            return pomodoroRepository.save(newConfig);
        });
    }

    // Obtener la configuración devolviendo el DTO
    public PomodoroConfigResponse getConfig(String username) {
        PomodoroConfiguration config = getOrCreateConfigEntity(username);
        return pomodoroMapper.toDto(config);
    }

    // Actualizar los tiempos devolviendo el DTO
    public PomodoroConfigResponse updateSettings(String username, PomodoroSettingsRequest request) {
        PomodoroConfiguration config = getOrCreateConfigEntity(username);

        config.setWorkDurationMinutes(request.getWorkDurationMinutes());
        config.setShortBreakDurationMinutes(request.getShortBreakDurationMinutes());
        config.setLongBreakDurationMinutes(request.getLongBreakDurationMinutes());
        config.setCyclesBeforeLongBreak(request.getCyclesBeforeLongBreak());

        pomodoroRepository.save(config);

        return pomodoroMapper.toDto(config);
    }

    // Actualizar la fase actual devolviendo el DTO
    public PomodoroConfigResponse updateState(String username, PomodoroStateRequest request) {
        PomodoroConfiguration config = getOrCreateConfigEntity(username);

        config.setTypeCycle(request.getTypeCycle());
        config.setActive(request.isActive());

        pomodoroRepository.save(config);

        return pomodoroMapper.toDto(config);
    }

    @Transactional
    public FinishResponse finishPomodoroCycle(String username) {
        List<Hero> activeHeroes = personRepository.findActiveHeroesByUsername(username);
        FinishResponse newResponse = new FinishResponse();

        if (activeHeroes.isEmpty()) {
            newResponse.setMessage("¡Lo has logrado! pero no tienes héroes. ¡Visita la Taberna para reclutar héroes y entrenarlos!");
            return newResponse;
        }

        for (Hero hero : activeHeroes) {
            if (hero.isState()) { // Solo afecta a los héroes que no han muerto
                //Se quita todo y se se sube de nivel
                hero.setPoisoned(false);
                hero.setPoisonTurns(0);
                hero.setStun(false);
                hero.setTimeStun(0);
                //Subir nivel para mejorar armadura y vida
                hero.levelUp();
            }
        }

        personRepository.saveAll(activeHeroes);

        newResponse.setMessage("¡Lo has logrado! Tus héroes han descansado, curado sus heridas por completo y subido de nivel.");
        return newResponse;
    }
}