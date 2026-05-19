package com.openwebinars.todo.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.openwebinars.todo.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
