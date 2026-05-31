package com.pomodoro_war.demo.services;

import com.pomodoro_war.demo.dtos.request.TaskRequest;
import com.pomodoro_war.demo.dtos.response.TaskResponse;
import com.pomodoro_war.demo.entities.auth.User;
import com.pomodoro_war.demo.entities.pomodoro.Task;
import com.pomodoro_war.demo.mappers.TaskMapper;
import com.pomodoro_war.demo.repositories.TaskRepository;
import com.pomodoro_war.demo.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;

    public List<TaskResponse> getAllTasks(String username) {
        return taskRepository.findByUserUsername(username).stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public TaskResponse createTask(String username, TaskRequest request) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        Task task = new Task();
        // Traducimos el DTO a la Entidad
        task.setName(request.getTitulo());
        task.setCompleted(request.getCompletada() != null && request.getCompletada());
        task.setDescription(request.getDescription());
        task.setDate(request.getDate() != null ? request.getDate() : LocalDate.now());
        task.setUser(user);

        return taskMapper.toDto(taskRepository.save(task));
    }

    @Transactional
    public TaskResponse updateTask(Long id, String username, TaskRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Misión no encontrada"));

        if (task.getUser() == null || !task.getUser().getUsername().equals(username)) {
            throw new IllegalArgumentException("No tienes permiso para eliminar esta misión");
        }

        // Actualizamos usando los datos del DTO si no vienen nulos
        if (request.getTitulo() != null) {
            task.setName(request.getTitulo());
        }
        if (request.getCompletada() != null) {
            task.setCompleted(request.getCompletada());
        }
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if (request.getDate() != null) {
            task.setDate(request.getDate());
        }

        return taskMapper.toDto(taskRepository.save(task));
    }

    @Transactional
    public void deleteTask(Long id, String username) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Misión no encontrada"));

        if (task.getUser() == null || !task.getUser().getUsername().equals(username)) {
            throw new IllegalArgumentException("No tienes permiso para eliminar esta misión");
        }

        taskRepository.delete(task);
    }
}