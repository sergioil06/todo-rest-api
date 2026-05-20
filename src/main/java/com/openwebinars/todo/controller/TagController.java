package com.openwebinars.todo.controller;

import java.nio.file.AccessDeniedException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.openwebinars.todo.dto.TagRequestDto;
import com.openwebinars.todo.dto.TagResponseDto;
import com.openwebinars.todo.dto.GetTaskDto;
import com.openwebinars.todo.model.Tag;
import com.openwebinars.todo.model.Task;
import com.openwebinars.todo.service.TagService;
import com.openwebinars.todo.service.TaskService;
import com.openwebinars.todo.users.User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/tag")
@RequiredArgsConstructor
@SecurityRequirement(name = "basicAuth")
@io.swagger.v3.oas.annotations.tags.Tag(name = "Tag", description = "Gestión de etiquetas")
public class TagController {

    private final TagService tagService;
    private final TaskService taskService;

    @Operation(summary = "Listar todas las etiquetas")
    @GetMapping
    public ResponseEntity<List<TagResponseDto>> getAll() {
        List<TagResponseDto> tags = tagService.findAll()
                .stream()
                .map(TagResponseDto::of)
                .toList();
        return ResponseEntity.ok(tags);
    }

    @Operation(summary = "Crear una nueva etiqueta")
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'GESTOR')")
    public ResponseEntity<TagResponseDto> create(@Valid @RequestBody TagRequestDto request) {
        Tag tag = tagService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(TagResponseDto.of(tag));
    }

    @Operation(summary = "Actualizar una etiqueta")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'GESTOR')") 
    public ResponseEntity<TagResponseDto> update(
            @PathVariable Long id, 
            @Valid @RequestBody TagRequestDto request
    ) {
        Tag updatedTag = tagService.edit(id, request);
        return ResponseEntity.ok(TagResponseDto.of(updatedTag));
    }

    @Operation(summary = "Eliminar una etiqueta")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'GESTOR')") 
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tagService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Asignar una etiqueta a una tarea")
    @PutMapping("/assign/{taskId}/{tagId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<GetTaskDto> addTag(
            @PathVariable Long taskId,
            @PathVariable Long tagId,
            @AuthenticationPrincipal User userLogueado
    ) throws AccessDeniedException {
        Task updatedTask = taskService.addTagToTask(taskId, tagId, userLogueado);
        return ResponseEntity.ok(GetTaskDto.of(updatedTask));
    }

    @Operation(summary = "Quitar una etiqueta de una tarea")
    @DeleteMapping("/remove/{taskId}/{tagId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<GetTaskDto> removeTag(
            @PathVariable Long taskId,
            @PathVariable Long tagId,
            @AuthenticationPrincipal User userLogueado
    ) throws AccessDeniedException {
        Task updatedTask = taskService.removeTagFromTask(taskId, tagId, userLogueado);
        return ResponseEntity.ok(GetTaskDto.of(updatedTask));
    }
}