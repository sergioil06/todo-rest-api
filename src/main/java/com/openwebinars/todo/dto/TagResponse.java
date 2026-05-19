package com.openwebinars.todo.dto;

import com.openwebinars.todo.model.Tag;

public record TagResponse(
    Long id,
    String name
) {
    public static TagResponse of(Tag tag) {
        return new TagResponse(tag.getId(), tag.getName());
    }
}