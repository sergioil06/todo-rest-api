package com.openwebinars.todo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskDashboardDto {
    private long totalTasks;        // Número total de tareas del usuario
    private long completedTasks;    // Tareas marcadas como completadas
    private long pendingTasks;      // Tareas pendientes
    private long highPriorityTasks; // Tareas que son de prioridad ALTA
}