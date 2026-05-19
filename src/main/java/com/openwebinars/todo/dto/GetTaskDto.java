package com.openwebinars.todo.dto;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

import com.openwebinars.todo.model.Tag;
import com.openwebinars.todo.model.Task;
import com.openwebinars.todo.model.TaskPriority;
import com.openwebinars.todo.users.NewUserResponse;

public record GetTaskDto(
        Long id,
        String title,
        String description,
        boolean completed,
        TaskPriority priority,
        LocalDateTime createdAt,
        LocalDateTime deadline,
        NewUserResponse author,
        String categoryName,
        Set<String> tags
) {
    public static GetTaskDto of(Task t) {
        return new GetTaskDto(
                t.getId(),
                t.getTitle(),
                t.getDescription(),
                t.isCompleted(),
                t.getPriority(),
                t.getCreatedAt(),
                t.getDeadline(),
                t.getAuthor() != null ? NewUserResponse.of(t.getAuthor()) : null,
                t.getCategory() != null ? t.getCategory().getTitle() : "Sin categoría",
                t.getTags() != null ? 
                    t.getTags().stream()
                        .map(Tag::getName)
                        .collect(Collectors.toSet()) : 
                    Set.of()
        );
    }
}	