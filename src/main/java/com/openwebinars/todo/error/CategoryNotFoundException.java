package com.openwebinars.todo.error;

public class CategoryNotFoundException extends RuntimeException {
    public CategoryNotFoundException(Long id) {
        super("La categoría con ID " + id + " no existe en el sistema.");
    }
}