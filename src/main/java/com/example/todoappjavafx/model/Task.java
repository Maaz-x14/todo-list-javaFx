package com.example.todoappjavafx.model;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Represents a single task in the TODO application.
 * Immutable ID ensures consistency across saves and reloads.
 */
public class Task {

    private final String id;
    private String title;
    private String description;
    private Priority priority;
    private LocalDate dueDate;
    private boolean completed;

    public Task(String title, String description, Priority priority, LocalDate dueDate) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.dueDate = dueDate;
        this.completed = false;
    }

    // Secondary constructor (for loading from persistence)
    public Task(String id, String title, String description, Priority priority, LocalDate dueDate, boolean completed) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.dueDate = dueDate;
        this.completed = completed;
    }

    // --- Getters & Setters ---
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    // --- Convenience methods ---
    public void toggleCompletion() {
        this.completed = !this.completed;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s, due: %s)",
                completed ? "✔" : " ",
                title,
                priority,
                dueDate != null ? dueDate : "N/A");
    }
}
