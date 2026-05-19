package com.openwebinars.todo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.openwebinars.todo.dto.CategoryDTO;
import com.openwebinars.todo.dto.CategoryRequest;
import com.openwebinars.todo.error.BusinessRuleException;
import com.openwebinars.todo.error.CategoryNotFoundException;
import com.openwebinars.todo.model.Category;
import com.openwebinars.todo.repos.CategoryRepository;
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoryDTO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
        return convertToDTO(category);
    }

    @Transactional
    public CategoryDTO createCategory(CategoryRequest categoryRequest) {
        if (categoryRequest.getTitle() == null || categoryRequest.getTitle().trim().isEmpty()) {
            throw new BusinessRuleException("El título de la categoría no puede estar vacío.");
        }

        Category category = new Category();
        category.setTitle(categoryRequest.getTitle().trim());
        category.setTasks(new ArrayList<>());

        Category savedCategory = categoryRepository.save(category);
        return convertToDTO(savedCategory);
    }

    @Transactional
    public CategoryDTO updateCategory(Long id, CategoryRequest categoryRequest) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
        
        if (categoryRequest.getTitle() == null || categoryRequest.getTitle().trim().isEmpty()) {
            throw new BusinessRuleException("El título no puede estar vacío.");
        }

        category.setTitle(categoryRequest.getTitle().trim());
        Category updatedCategory = categoryRepository.save(category);
        return convertToDTO(updatedCategory);
    }

    @Transactional
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new CategoryNotFoundException(id);
        }
        categoryRepository.deleteById(id);
    }

    private CategoryDTO convertToDTO(Category category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setTitle(category.getTitle());
        return dto;
    }
}