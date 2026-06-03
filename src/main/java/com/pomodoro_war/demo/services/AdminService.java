package com.pomodoro_war.demo.services;

import com.pomodoro_war.demo.dtos.response.UserAdminResponse;
import com.pomodoro_war.demo.entities.auth.User;
import com.pomodoro_war.demo.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProfileService profileService;

    // Método de seguridad interno
    private void verifyAdminRole(String adminUsername) {
        User admin = userRepository.findById(adminUsername)
                .orElseThrow(() -> new IllegalArgumentException("Administrador no encontrado."));
        if (!"ADMIN".equals(admin.getRole())) {
            throw new RuntimeException("Acceso denegado: No tienes rango de Administrador.");
        }
    }

    public List<UserAdminResponse> getAllUsers(String adminUsername) {
        verifyAdminRole(adminUsername);

        return userRepository.findAll().stream()
                .map(user -> new UserAdminResponse(
                        user.getUsername(),
                        user.getEmail(),
                        user.getMasterName(),
                        user.getRole()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateUserAsAdmin(String adminUsername, String targetUsername, String newEmail, String newPassword) {
        verifyAdminRole(adminUsername);

        User targetUser = userRepository.findById(targetUsername)
                .orElseThrow(() -> new IllegalArgumentException("Usuario objetivo no encontrado."));

        if (newEmail != null && !newEmail.isEmpty()) {
            targetUser.setEmail(newEmail);
        }

        if (newPassword != null && !newPassword.isEmpty()) {
            targetUser.setPassword(passwordEncoder.encode(newPassword));
        }

        userRepository.save(targetUser);
    }

    @Transactional
    public void deleteUserAsAdmin(String adminUsername, String targetUsername) {
        verifyAdminRole(adminUsername);

        // Protegemos a los admins para que no se borren entre ellos o a sí mismos por accidente
        User targetUser = userRepository.findById(targetUsername)
                .orElseThrow(() -> new IllegalArgumentException("Usuario objetivo no encontrado."));
        if ("ADMIN".equals(targetUser.getRole())) {
            throw new IllegalArgumentException("No puedes eliminar a otro Administrador desde el panel.");
        }

        // Reutilizamos el método deleteAccount que ya programamos en ProfileService
        // para que borre limpiamente héroes, tareas, cementerio y mundo.
        profileService.deleteAccount(targetUsername);
    }
}