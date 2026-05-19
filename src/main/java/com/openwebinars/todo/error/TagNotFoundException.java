package com.openwebinars.todo.error;


public class TagNotFoundException extends RuntimeException {
    public TagNotFoundException(Long id) {
        super("La etiqueta con ID " + id + " no existe en el sistema.");
    }
}
