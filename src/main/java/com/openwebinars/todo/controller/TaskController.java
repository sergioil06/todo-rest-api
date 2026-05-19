package com.openwebinars.todo.controller;



import java.nio.file.AccessDeniedException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.openwebinars.todo.dto.EditTaskCommand;
import com.openwebinars.todo.dto.GetTaskDto;
import com.openwebinars.todo.dto.TaskDashboardDto;
import com.openwebinars.todo.model.Task;
import com.openwebinars.todo.model.TaskPriority;
import com.openwebinars.todo.service.TaskService;
import com.openwebinars.todo.users.User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/task")
@RequiredArgsConstructor
@SecurityRequirement(name = "basicAuth")
@Tag(name = "Task", description = "Endpoints para la gestión de tareas personales")
public class TaskController {

    private final TaskService taskService;

    @Operation(summary = "Listar tareas del usuario (opcionalmente filtradas por prioridad)")
    @GetMapping
    public ResponseEntity<List<GetTaskDto>> getAll(
            @AuthenticationPrincipal User author,
            @RequestParam(required = false) TaskPriority priority
    ) {
        List<GetTaskDto> tasks = taskService.findByAuthor(author)
                .stream()
                .filter(t -> priority == null || t.getPriority() == priority)
                .map(GetTaskDto::of) // Usando tu método .of() nativo
                .toList();
        
        return ResponseEntity.ok(tasks);
    }

    @Operation(summary = "Obtener una tarea específica por su ID")
    @PostAuthorize("returnObject.body.author.username == authentication.principal.username")
    @GetMapping("/{id}")
    public ResponseEntity<GetTaskDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(GetTaskDto.of(taskService.findById(id)));
    }

    @Operation(summary = "Crear una nueva tarea")
    @PostMapping
    public ResponseEntity<GetTaskDto> create(
            @RequestBody EditTaskCommand cmd,
            @AuthenticationPrincipal User author
    ) {
        Task task = taskService.save(cmd, author);
        return ResponseEntity.status(HttpStatus.CREATED).body(GetTaskDto.of(task));
    }

    @Operation(summary = "Editar una tarea existente")
    @PutMapping("/{id}")
    public ResponseEntity<GetTaskDto> edit(
            @RequestBody EditTaskCommand cmd,
            @PathVariable Long id,
            @AuthenticationPrincipal User userLogueado
    ) throws AccessDeniedException {
        Task task = taskService.edit(cmd, id, userLogueado);
        return ResponseEntity.ok(GetTaskDto.of(task));
    }

    @Operation(summary = "Eliminar una tarea")
    @ApiResponse(responseCode = "204", description = "Tarea eliminada con éxito")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal User userLogueado
    ) throws AccessDeniedException {
        taskService.delete(id, userLogueado);
        return ResponseEntity.noContent().build();
    }
    
    // --- NUEVO ENDPOINT DE BÚSQUEDA AVANZADA (Exigido por el PDF) ---

    @Operation(summary = "Buscar mis tareas que tengan ciertas etiquetas")
    @GetMapping("/search/by-tags")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'GESTOR')")
    public ResponseEntity<List<GetTaskDto>> searchByTags(
            @RequestParam List<Long> tagIds,
            @AuthenticationPrincipal User userLogueado
    ) {
        // 1. Traemos las entidades filtradas desde el servicio de forma segura
        List<Task> tasks = taskService.buscarTareasPorEtiquetas(tagIds, userLogueado);
        
        // 2. Mapeamos de forma limpia usando tu GetTaskDto::of que no da fallos de inferencia
        List<GetTaskDto> dtoList = tasks.stream()
                .map(GetTaskDto::of)
                .toList();

        return ResponseEntity.ok(dtoList);
    }
    
    @Operation(summary = "Obtener el cuadro de mandos estadístico (Dashboard) de mis tareas")
    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'GESTOR')")
    public ResponseEntity<TaskDashboardDto> getDashboard(
            @AuthenticationPrincipal User userLogueado
    ) {
        TaskDashboardDto dashboard = taskService.obtenerDashboardUsuario(userLogueado);
        return ResponseEntity.ok(dashboard);
    }
}