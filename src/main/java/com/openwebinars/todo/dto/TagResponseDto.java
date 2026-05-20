package com.openwebinars.todo.dto;

import com.openwebinars.todo.model.Tag;

public record TagResponseDto(
    Long id,
    String name
) {
    public static TagResponseDto of(Tag tag) {
        return new TagResponseDto(tag.getId(), tag.getName());
    }
}