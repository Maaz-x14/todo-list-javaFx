package com.example.todoappjavafx.repository;

import java.util.List;
import java.util.Optional;

/**
 * Generic repository interface defining CRUD operations.
 * @param <T> The entity type.
 */
public interface Repository<T> {

    void save(T entity);

    void update(T entity);

    void delete(String id);

    Optional<T> findById(String id);

    List<T> findAll();
}
