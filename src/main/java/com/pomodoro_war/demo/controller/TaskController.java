package com.pomodoro_war.demo.controller;

import com.pomodoro_war.demo.dtos.request.DeleteTaskRequest;
import com.pomodoro_war.demo.dtos.request.TaskRequest;
import com.pomodoro_war.demo.dtos.response.TaskResponse;
import com.pomodoro_war.demo.services.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tareas")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    // Rutas base limpias
    @GetMapping
    public ResponseEntity<List<TaskResponse>> getTasks(Authentication auth) {
        return ResponseEntity.ok(taskService.getAllTasks(auth.getName()));
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@RequestBody TaskRequest request, Authentication auth) {
        return ResponseEntity.ok(taskService.createTask(auth.getName(), request));
    }

    // Sin la barra al final del ID
    @PatchMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long id,
            @RequestBody TaskRequest request,
            Authentication auth) {
        return ResponseEntity.ok(taskService.updateTask(id, auth.getName(), request));
    }

    // Manteniendo la seguridad del borrado por POST y leyendo el ID, pero con ruta limpia
    @PostMapping("/borrar")
    public ResponseEntity<Map<String, String>> deleteTask(@RequestBody DeleteTaskRequest request, Authentication auth) {
        taskService.deleteTask(request.getId(), auth.getName());
        return ResponseEntity.ok(Map.of("message", "Misión eliminada correctamente"));
    }
}