package com.openwebinars.todo.dto;

import jakarta.validation.constraints.NotBlank;

public record TagRequestDto(
    @NotBlank(message = "El nombre de la etiqueta no puede estar vacío") 
    String name
) {}
