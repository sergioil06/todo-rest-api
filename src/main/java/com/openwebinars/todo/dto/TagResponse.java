package com.openwebinars.todo.dto;

import com.openwebinars.todo.model.Tag;

public record TagResponse(
    Long id,
    String name
) {
    // Mapeador estático: convierte la Entidad en DTO de forma limpia
    public static TagResponse of(Tag tag) {
        return new TagResponse(tag.getId(), tag.getName());
    }
}