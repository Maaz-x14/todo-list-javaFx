package com.example.todoappjavafx.service;

import com.example.todoappjavafx.model.Priority;
import com.example.todoappjavafx.model.Task;
import com.example.todoappjavafx.repository.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service layer for managing task business logic.
 */
public class TaskService {
    private final Repository<Task> repository;

    public TaskService(Repository<Task> repository) {
        this.repository = repository;
    }

    // Create
    public void addTask(Task task) {
        repository.save(task);
    }

    // Read
    public List<Task> getAllTasks() {
        return repository.findAll();
    }

    public Optional<Task> getTaskById(String id) {
        return repository.findById(id);
    }

    // Update
    public void updateTask(Task updatedTask) {
        repository.update(updatedTask);
    }

    // Delete
    public void deleteTask(String id) {
        repository.delete(id);
    }

    // Toggle completion status
    public void toggleTaskCompletion(String id) {
        repository.findById(id).ifPresent(task -> {
            task.setCompleted(!task.isCompleted());
            repository.update(task);
        });
    }

    // Filter by priority
    public List<Task> filterByPriority(Priority priority) {
        return repository.findAll()
                .stream()
                .filter(task -> task.getPriority() == priority)
                .collect(Collectors.toList());
    }


    // Calculate completion progress
    public double getCompletionProgress() {
        List<Task> allTasks = repository.findAll();
        if (allTasks.isEmpty()) return 0;
        long completed = allTasks.stream().filter(Task::isCompleted).count();
        return (completed * 100.0) / allTasks.size();
    }
}
