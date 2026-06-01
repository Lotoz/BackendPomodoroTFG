package com.pomodoro_war.demo.security.services;


import com.pomodoro_war.demo.entities.auth.User;
import com.pomodoro_war.demo.repositories.UserRepository;
import com.pomodoro_war.demo.security.dtos.AuthResponse;
import com.pomodoro_war.demo.security.dtos.LoginRequest;
import com.pomodoro_war.demo.security.dtos.RegisterRequest;
import com.pomodoro_war.demo.security.dtos.ResetPasswordRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JavaMailSender mailSender;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsById(request.getUsername())) {
            throw new IllegalArgumentException("El nombre de usuario ya existe.");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setMasterName(request.getMasterName()); // Guardamos el maestro
        userRepository.save(user);

        String jwtToken = jwtService.generateToken(user.getUsername());
        return new AuthResponse(jwtToken, user.getMasterName());
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findById(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Contraseña incorrecta.");
        }

        // Generamos el token
        String jwtToken = jwtService.generateToken(user.getUsername());
        return new AuthResponse(jwtToken, user.getMasterName());
    }

    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ese correo."));

        // Generamos un código de 6 dígitos
        String code = String.format("%06d", new Random().nextInt(999999));
        user.setResetCode(code);
        userRepository.save(user);

        // Enviamos el correo
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Recuperación de contraseña - Pomodoro War");
        message.setText("Saludos Comandante,\n\nTu código para forjar una nueva contraseña es: " + code + "\n\nSi no has solicitado esto, ignora este mensaje.");
        mailSender.send(message);
    }


    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));

        if (user.getResetCode() == null || !user.getResetCode().equals(request.getCode())) {
            throw new IllegalArgumentException("Código inválido o expirado.");
        }

        // Actualizamos la contraseña y borramos el código
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetCode(null);
        userRepository.save(user);
    }

}