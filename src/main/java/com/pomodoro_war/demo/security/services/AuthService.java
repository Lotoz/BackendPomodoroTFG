package com.pomodoro_war.demo.security.services;


import com.pomodoro_war.demo.entities.auth.User;
import com.pomodoro_war.demo.repositories.UserRepository;
import com.pomodoro_war.demo.security.dtos.AuthResponse;
import com.pomodoro_war.demo.security.dtos.LoginRequest;
import com.pomodoro_war.demo.security.dtos.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

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
}