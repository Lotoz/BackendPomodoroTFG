package com.pomodoro_war.demo.security.services;

import com.pomodoro_war.demo.entities.auth.User;
import com.pomodoro_war.demo.repositories.UserRepository;
import com.pomodoro_war.demo.security.dtos.AuthResponse;
import com.pomodoro_war.demo.security.dtos.LoginRequest;
import com.pomodoro_war.demo.security.dtos.RegisterRequest;
import com.pomodoro_war.demo.security.dtos.ResetPasswordRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    //Leemos las variables de Resend
    @Value("${resend.api.key:NO_KEY}")
    private String resendApiKey;

    @Value("${resend.sender.email:NO_EMAIL}")
    private String senderEmail;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsById(request.getUsername())) {
            throw new IllegalArgumentException("El nombre de usuario ya existe.");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setMasterName(request.getMasterName());

        // Todos los reclutados son usuarios, los admin se dan de alta manualmente desde el admin
        user.setRole("USER");

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

        String jwtToken = jwtService.generateToken(user.getUsername());
        return new AuthResponse(jwtToken, user.getMasterName());
    }

    // SOLICITAR CÓDIGO (VÍA API REST DE RESEND)
    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ese correo."));

        String code = String.format("%06d", new Random().nextInt(999999));
        user.setResetCode(code);
        userRepository.save(user);

        // Llamamos al nuevo método de Resend
        sendEmailViaResend(user.getEmail(), user.getUsername(), code);
        System.out.println("usuario: " + user.getUsername() + " code:" + code);
    }

    // RESTABLECER CONTRASEÑA
    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));

        if (user.getResetCode() == null || !user.getResetCode().equals(request.getCode())) {
            throw new IllegalArgumentException("Código inválido o expirado.");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetCode(null);
        userRepository.save(user);
    }

    // ==========================================
    // LLAMADA API REST A RESEND
    // ==========================================
    private void sendEmailViaResend(String toEmail, String username, String code) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.resend.com/emails";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // Resend requiere la palabra "Bearer" antes de la clave
        headers.set("Authorization", "Bearer " + resendApiKey);
        headers.set("accept", "application/json");

        // Creamos el JSON adaptado a Resend (es más simple que Brevo)
        String body = "{"
                + "\"from\": \"" + senderEmail + "\","
                + "\"to\": [\"" + toEmail + "\"],"
                + "\"subject\": \"El Cuervo Mensajero: Tu nueva llave de la Fortaleza\","
                + "\"html\": \"<div style='font-family: sans-serif; background-color: #1a0d2e; color: #f0e6ff; padding: 30px; border-radius: 10px; border: 2px solid #7d33cc; text-align: center; max-width: 500px; margin: 0 auto;'>"
                + "<h2 style='color: #ffd700; text-transform: uppercase;'>Saludos, Comandante " + username + "</h2>"
                + "<p style='font-size: 16px;'>Hemos recibido una solicitud para forjar una nueva llave mágica para tu cuenta.</p>"
                + "<p style='font-size: 16px;'>Tu pergamino de recuperación dicta el siguiente código:</p>"
                + "<div style='margin: 20px auto; padding: 15px; background-color: #3d2563; border: 2px dashed #b366ff; display: inline-block; font-size: 32px; font-weight: bold; letter-spacing: 5px; color: #00ff88; border-radius: 8px;'>" + code + "</div>"
                + "<p style='font-size: 12px; color: #c9b8ff; margin-top: 30px;'>Si los oscuros no han solicitado esto, ignora este mensaje y blande tu espada con honor.</p>"
                + "</div>\""
                + "}";

        HttpEntity<String> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
            System.out.println("Cuervo enviado con éxito vía Resend API. Status: " + response.getStatusCode());
        } catch (Exception e) {
            System.err.println("Error al contactar con la API de Resend: " + e.getMessage());
            throw new RuntimeException("Nuestros cuervos mensajeros han sido interceptados. Inténtalo de nuevo más tarde.");
        }
    }
}