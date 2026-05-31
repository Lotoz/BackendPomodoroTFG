package com.pomodoro_war.demo.services;

import com.pomodoro_war.demo.dtos.request.UpdatePasswordRequest;
import com.pomodoro_war.demo.dtos.request.UpdateProfileRequest;
import com.pomodoro_war.demo.dtos.response.ProfileConfigResponse;
import com.pomodoro_war.demo.entities.auth.User;
import com.pomodoro_war.demo.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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
}