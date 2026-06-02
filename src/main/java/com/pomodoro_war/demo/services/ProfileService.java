package com.pomodoro_war.demo.services;

import com.pomodoro_war.demo.dtos.request.UpdatePasswordRequest;
import com.pomodoro_war.demo.dtos.request.UpdateProfileRequest;
import com.pomodoro_war.demo.dtos.response.ProfileConfigResponse;
import com.pomodoro_war.demo.entities.auth.User;
import com.pomodoro_war.demo.entities.enums.ZoneType;
import com.pomodoro_war.demo.repositories.*;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final WorldProgressRepository worldProgressRepository;
    private final TaskRepository taskRepository;
    private final PersonRepository personRepository;
    private final FallenHeroRepository fallenHeroRepository;

    @Transactional
    public ProfileConfigResponse updateProfile(String username, UpdateProfileRequest request) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new IllegalArgumentException("Comandante no encontrado"));

        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            user.setEmail(request.getEmail());
        }
        if (request.getMasterName() != null && !request.getMasterName().isEmpty()) {
            user.setMasterName(request.getMasterName());
        }

        userRepository.save(user);
        return new ProfileConfigResponse("Perfil actualizado con éxito. ¡Tu maestro ha cambiado!");
    }

    @Transactional
    public ProfileConfigResponse updatePassword(String username, UpdatePasswordRequest request) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new IllegalArgumentException("Comandante no encontrado"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("La contraseña actual es incorrecta");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Las contraseñas nuevas no coinciden");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return new ProfileConfigResponse("Contraseña actualizada con éxito");
    }

    @Transactional
    public ProfileConfigResponse resetProgress(String username) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new IllegalArgumentException("Comandante no encontrado"));

        //Borro de entidades del usurio
        taskRepository.deleteAllByUsername(username);
        personRepository.deleteAllByUsername(username);

        // 2. Reiniciamos el progreso del mundo (Lo enviamos de vuelta al Mundo 1)
        worldProgressRepository.findByUserUsername(username).ifPresent(progress -> {
            progress.setCurrentZone(ZoneType.INITIAL);
            progress.setCurrentStage(1);
            worldProgressRepository.save(progress);
        });

        userRepository.save(user);

        return new ProfileConfigResponse("Tu progreso ha sido reducido a cenizas. Empiezas desde cero.");
    }

    @Transactional
    public ProfileConfigResponse deleteAccount(String username) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new IllegalArgumentException("Comandante no encontrado"));

        //Aseguramos el borrado de todos los datos del usurio
        personRepository.deleteAllByUsername(username);
        worldProgressRepository.deleteAllByUsername(username);
        fallenHeroRepository.deleteAllByUsername(username);
        userRepository.delete(user);
        return new ProfileConfigResponse("La Fortaleza ha sido destruida. Tu cuenta ha sido eliminada.");
    }
}