package com.voice_ai_app.product_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.voice_ai_app.product_app.model.Product;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Spring Data JPA derives the query from the method name:
    // WHERE LOWER(name) LIKE LOWER(CONCAT('%', :name, '%'))
    List<Product> findByNameContainingIgnoreCase(String name);

    // WHERE LOWER(category) = LOWER(:category)
    List<Product> findByCategoryIgnoreCase(String category);

    // Custom JPQL query to get all distinct categories
    @Query("SELECT DISTINCT p.category FROM Product p WHERE p.category IS NOT NULL ORDER BY p.category")
    List<String> findAllCategories();
}
