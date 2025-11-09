// package com.example.todoappjavafx.repository; (make sure package is correct)
package com.example.todoappjavafx.repository;

import com.example.todoappjavafx.model.Task;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Concrete repository implementation that persists tasks in a JSON file.
 * This is the correct version that:
 * 1. Implements Repository<Task>
 * 2. Takes a file path in its constructor (Dependency Injection)
 * 3. Includes LocalDate adapters for Gson
 */
public class JsonTaskRepository implements Repository<Task> { // 🛑 FIX 1: Implements generic interface

    private final Path filePath;
    private final Gson gson;
    private final Type taskListType = new TypeToken<List<Task>>() {}.getType(); // 🛑 FIX 2: Use Task type

    public JsonTaskRepository(String filePath) {
        this.filePath = Path.of(filePath); // ✅ Correct: Path is injected
        this.gson = new GsonBuilder()
                // ✅ CRITICAL: This is the adapter you deleted.
                .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (date, type, context) ->
                        new JsonPrimitive(date.toString())
                )
                .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, type, context) ->
                        LocalDate.parse(json.getAsString())
                )
                .setPrettyPrinting()
                .create();
        initializeFile();
    }

    private void initializeFile() {
        try {
            if (Files.notExists(filePath.getParent())) {
                Files.createDirectories(filePath.getParent());
            }
            if (Files.notExists(filePath)) {
                Files.createFile(filePath);
                saveTasks(new ArrayList<>()); // Write empty list to start
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize task repository file.", e);
        }
    }

    private List<Task> loadTasks() {
        try (FileReader reader = new FileReader(filePath.toFile())) {
            List<Task> tasks = gson.fromJson(reader, taskListType);
            return tasks != null ? tasks : new ArrayList<>();
        } catch (IOException e) {
            // Handle file not found or empty file gracefully on first load
            if (e instanceof java.io.FileNotFoundException || Files.notExists(filePath)) {
                return new ArrayList<>();
            }
            throw new RuntimeException("Failed to load tasks from JSON file.", e);
        }
    }

    private void saveTasks(List<Task> tasks) {
        try (FileWriter writer = new FileWriter(filePath.toFile())) {
            gson.toJson(tasks, writer);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save tasks to JSON file.", e);
        }
    }

    @Override
    public void save(Task task) {
        List<Task> tasks = loadTasks();
        tasks.add(task);
        saveTasks(tasks);
    }

    @Override
    public void update(Task updatedTask) {
        List<Task> tasks = loadTasks();
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getId().equals(updatedTask.getId())) {
                tasks.set(i, updatedTask);
                saveTasks(tasks);
                return;
            }
        }
    }

    @Override
    public void delete(String id) {
        List<Task> tasks = loadTasks();
        tasks.removeIf(task -> task.getId().equals(id));
        saveTasks(tasks);
    }

    @Override
    public Optional<Task> findById(String id) {
        return loadTasks().stream()
                .filter(task -> task.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<Task> findAll() {
        return loadTasks();
    }
}