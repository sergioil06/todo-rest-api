package com.openwebinars.todo.dto;

import com.openwebinars.todo.model.TaskPriority;

import java.time.LocalDateTime;

public record EditTaskCommand(
    String title,
    String description,
    LocalDateTime deadline,
    TaskPriority priority,
    Long categoryId,
    Boolean completed
) {}
