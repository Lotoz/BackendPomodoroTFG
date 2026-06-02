package com.pomodoro_war.demo.controller;

import com.pomodoro_war.demo.dtos.request.UpdatePasswordRequest;
import com.pomodoro_war.demo.dtos.request.UpdateProfileRequest;
import com.pomodoro_war.demo.dtos.response.ProfileConfigResponse;
import com.pomodoro_war.demo.services.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @PutMapping("/profile")
    public ResponseEntity<ProfileConfigResponse> updateProfile(
            @RequestBody UpdateProfileRequest request,
            Authentication auth) {
        return ResponseEntity.ok(profileService.updateProfile(auth.getName(), request));
    }

    @PutMapping("/password")
    public ResponseEntity<ProfileConfigResponse> updatePassword(
            @RequestBody UpdatePasswordRequest request,
            Authentication auth) {
        return ResponseEntity.ok(profileService.updatePassword(auth.getName(), request));
    }

    @PostMapping("/reset-progress")
    public ResponseEntity<ProfileConfigResponse> resetProgress(Authentication auth) {
        return ResponseEntity.ok(profileService.resetProgress(auth.getName()));
    }

    @PostMapping("/delete-account")
    public ResponseEntity<ProfileConfigResponse> deleteAccount(Authentication auth) {
        return ResponseEntity.ok(profileService.deleteAccount(auth.getName()));
    }
}