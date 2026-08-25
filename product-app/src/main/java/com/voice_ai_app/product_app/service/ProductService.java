package com.voice_ai_app.product_app.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.voice_ai_app.product_app.exception.ProductNotFoundException;
import com.voice_ai_app.product_app.model.Product;
import com.voice_ai_app.product_app.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;   

    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        log.debug("Fetching all products");
        return productRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Product getProductById(Long id) {
        log.debug("Fetching product by id={}", id);
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<Product> searchByName(String name) {
        log.debug("Searching products by name='{}'", name);
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    @Transactional(readOnly = true)
    public List<Product> getByCategory(String category) {
        log.debug("Fetching products by category='{}'", category);
        return productRepository.findByCategoryIgnoreCase(category);
    }

    @Transactional(readOnly = true)
    public List<String> getAllCategories() {
        log.debug("Fetching all categories");
        return productRepository.findAllCategories();
    }

    public Product createProduct(Product product) {
        log.debug("Creating product: {}", product.getName());
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product details) {
        log.debug("Updating product id={}", id);
        Product product = getProductById(id);
        product.setName(details.getName());
        product.setDescription(details.getDescription());
        product.setPrice(details.getPrice());
        product.setCategory(details.getCategory());
        product.setStockQuantity(details.getStockQuantity());
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        log.debug("Deleting product id={}", id);
        Product product = getProductById(id);
        productRepository.delete(product);
    }
}
