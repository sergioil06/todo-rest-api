package com.openwebinars.todo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.openwebinars.todo.dto.CategoryDto;
import com.openwebinars.todo.dto.CategoryRequestDto;
import com.openwebinars.todo.service.CategoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
@RestController
@SecurityRequirement(name = "basicAuth")
@RequestMapping("/category") 
@Tag(name = "Categorías", description = "Controlador para la gestión de categorías de tareas")
public class CategoryController {

 private final CategoryService categoryService;

 public CategoryController(CategoryService categoryService) {
     this.categoryService = categoryService;
 }

 @GetMapping
 @Operation(summary = "Listar todas las categorías", description = "Permite a cualquier usuario autenticado ver la lista de categorías.")
 public ResponseEntity<List<CategoryDto>> getAll() {
     return ResponseEntity.ok(categoryService.getAllCategories());
 }

 @GetMapping("/{id}")
 @Operation(summary = "Obtener una categoría por ID")
 public ResponseEntity<CategoryDto> getById(
         @Parameter(description = "ID de la categoría a buscar") @PathVariable Long id) {
     return ResponseEntity.ok(categoryService.getCategoryById(id));
 }

 @PostMapping
 @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_GESTOR')") 
 @Operation(summary = "Crear una nueva categoría", description = "Recibe un CategoryRequestDto sin ID. Acceso restringido a ADMIN y GESTOR.")
 public ResponseEntity<CategoryDto> create(@RequestBody CategoryRequestDto categoryRequest) {
     CategoryDto created = categoryService.createCategory(categoryRequest);
     return new ResponseEntity<>(created, HttpStatus.CREATED);
 }

 @PutMapping("/{id}")
 @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_GESTOR')")
 @Operation(summary = "Actualizar una categoría existente", description = "Recibe las modificaciones en un CategoryRequestDto. Acceso restringido a ADMIN y GESTOR.")
 public ResponseEntity<CategoryDto> update(
         @Parameter(description = "ID de la categoría a modificar") @PathVariable Long id,
         @RequestBody CategoryRequestDto categoryRequest) {
     return ResponseEntity.ok(categoryService.updateCategory(id, categoryRequest));
 }

 @DeleteMapping("/{id}")
 @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_GESTOR')")
 @Operation(summary = "Eliminar una categoría", description = "Elimina de forma permanente una categoría por su ID. Acceso restringido a ADMIN y GESTOR.")
 public ResponseEntity<Void> delete(
         @Parameter(description = "ID de la categoría a borrar") @PathVariable Long id) {
     categoryService.deleteCategory(id);
     return ResponseEntity.noContent().build();
 }
}