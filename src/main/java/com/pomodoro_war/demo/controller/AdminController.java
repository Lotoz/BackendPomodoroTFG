package com.pomodoro_war.demo.controller;

import com.pomodoro_war.demo.dtos.response.UserAdminResponse;
import com.pomodoro_war.demo.services.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // Obtener lista de todos los usuarios
    @GetMapping("/users")
    public ResponseEntity<List<UserAdminResponse>> getAllUsers(Authentication auth) {
        return ResponseEntity.ok(adminService.getAllUsers(auth.getName()));
    }
    // Actualizar correo o contraseña de un usuario
    @PatchMapping("/users/{username}")
    public ResponseEntity<?> updateUser(
            @PathVariable String username,
            @RequestBody Map<String, String> updates,
            Authentication auth) {

        String newEmail = updates.get("email");
        String newPassword = updates.get("password");

        adminService.updateUserAsAdmin(auth.getName(), username, newEmail, newPassword);
        return ResponseEntity.ok(Map.of("message", "Usuario " + username + " modificado con éxito por decreto del Administrador."));
    }

    // Eliminar a un usuario completamente
    @DeleteMapping("/users/{username}")
    public ResponseEntity<?> deleteUser(
            @PathVariable String username,
            Authentication auth) {

        adminService.deleteUserAsAdmin(auth.getName(), username);
        return ResponseEntity.ok(Map.of("message", "El usuario " + username + " ha sido desterrado de la Fortaleza."));
    }
}