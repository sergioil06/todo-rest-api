package com.openwebinars.todo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Modelo para registrar una nueva categoría")
public class CategoryRequestDto {

    @Schema(description = "Título único de la categoría", example = "Trabajo", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;
}
