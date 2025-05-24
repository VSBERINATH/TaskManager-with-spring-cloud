package com.taskmanager.taskservice.service;

import com.taskmanager.taskservice.entity.Task;
import com.taskmanager.taskservice.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    public Task createTask(Task task) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        task.setAssignedUser(username); // EMPLOYEE creates task for self
        return taskRepository.save(task);
    }

    public Task getTask(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
    }

    public Task updateTask(Long id, Task task) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        String role = auth.getAuthorities().stream().findFirst().get().getAuthority();

        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!role.equals("ROLE_ADMIN") && !existingTask.getAssignedUser().equals(username)) {
            throw new RuntimeException("Unauthorized: Only ADMIN or task owner can update");
        }

        existingTask.setTitle(task.getTitle());
        existingTask.setDescription(task.getDescription());
        existingTask.setStatus(task.getStatus());
        return taskRepository.save(existingTask);
    }

    public void deleteTask(Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        String role = auth.getAuthorities().stream().findFirst().get().getAuthority();

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!role.equals("ROLE_ADMIN") && !task.getAssignedUser().equals(username)) {
            throw new RuntimeException("Unauthorized: Only ADMIN or task owner can delete");
        }

        taskRepository.delete(task);
    }
}