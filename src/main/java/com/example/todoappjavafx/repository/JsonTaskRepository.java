package com.example.todoappjavafx.repository;

import com.example.todoappjavafx.model.Task;
import com.example.todoappjavafx.util.JsonUtil;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Handles persistence of tasks to a JSON file.
 * Keeps things simple but persistent across app restarts.
 */
public class JsonTaskRepository implements Repository<Task> {

    private static final String FILE_PATH = "tasks.json";
    private final Type taskListType = new TypeToken<List<Task>>() {}.getType();

    private List<Task> cache;

    public JsonTaskRepository() {
        cache = JsonUtil.readJson(FILE_PATH, taskListType);
        if (cache == null) cache = new ArrayList<>();
    }

    @Override
    public void save(Task task) {
        cache.add(task);
        persist();
    }

    @Override
    public void update(Task updatedTask) {
        cache.replaceAll(t -> t.getId().equals(updatedTask.getId()) ? updatedTask : t);
        persist();
    }

    @Override
    public void delete(String id) {
        cache.removeIf(t -> t.getId().equals(id));
        persist();
    }

    @Override
    public Optional<Task> findById(String id) {
        return cache.stream().filter(t -> t.getId().equals(id)).findFirst();
    }

    @Override
    public List<Task> findAll() {
        return new ArrayList<>(cache); // return copy
    }

    private void persist() {
        JsonUtil.writeJson(FILE_PATH, cache);
    }
}
