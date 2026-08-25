package com.voice_ai_app.mcp_server.tools;

import org.springframework.core.ParameterizedTypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.voice_ai_app.mcp_server.dto.ProductDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.util.List;

/**
 * This class defines the AI tools that the MCP Server exposes.
 *
 * Spring AI's MCP Server starter scans for beans with @Tool annotated methods
 * and automatically registers them as MCP tools. When an MCP Client sends a
 * tool-call request, the server invokes the corresponding method here.
 *
 * Each method:
 *   1. Calls the Product Application's REST API via RestClient
 *   2. Returns a JSON string that the LLM can understand
 *   3. Catches errors and returns a human-readable error message
 */
@Service
public class ProductTools {

    private static final Logger log = LoggerFactory.getLogger(ProductTools.class);

    private final RestClient productRestClient;
    private final ObjectMapper objectMapper;

    public ProductTools(RestClient productRestClient, ObjectMapper objectMapper) {
        this.productRestClient = productRestClient;
        this.objectMapper = objectMapper;
    }

    @Tool(description = """
            List all products available in the store.
            Returns each product's ID, name, price, category, and stock quantity.
            Use this when the user asks to see all products or browse the catalog.
            """)
    public String getAllProducts() {
        log.info("MCP Tool called: getAllProducts");
        try {
            List<ProductDto> products = productRestClient.get()
                    .uri("/api/products")
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<ProductDto>>() {});
            return objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(products);
        } catch (Exception e) {
            log.error("Error in getAllProducts", e);
            return "Error fetching products: " + e.getMessage();
        }
    }

    @Tool(description = """
            Get detailed information about a specific product by its numeric ID.
            Returns the product's name, description, price, category, and stock quantity.
            Use this when the user asks about a specific product.
            """)
    public String getProductById(
            @ToolParam(description = "The unique numeric ID of the product") Long id) {
        log.info("MCP Tool called: getProductById(id={})", id);
        try {
            ProductDto product = productRestClient.get()
                    .uri("/api/products/{id}", id)
                    .retrieve()
                    .body(ProductDto.class);
            return objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(product);
        } catch (Exception e) {
            log.error("Error in getProductById(id={})", id, e);
            return "Error fetching product: " + e.getMessage();
        }
    }

    @Tool(description = """
            Search for products by name. The search is case-insensitive and matches
            partial names. Returns a list of matching products.
            Use this when the user wants to find a product by name.
            """)
    public String searchProducts(
            @ToolParam(description = "The search term to match against product names") String name) {
        log.info("MCP Tool called: searchProducts(name='{}')", name);
        try {
            List<ProductDto> products = productRestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/products/search")
                            .queryParam("name", name)
                            .build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<ProductDto>>() {});
            return objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(products);
        } catch (Exception e) {
            log.error("Error in searchProducts(name='{}')", name, e);
            return "Error searching products: " + e.getMessage();
        }
    }

    @Tool(description = """
            Get all products in a specific category.
            Valid categories include: Laptops, Smartphones, Audio, Monitors, Accessories.
            Use this when the user asks to see products in a category.
            """)
    public String getProductsByCategory(
            @ToolParam(description = "The category name, e.g. 'Laptops', 'Audio', 'Smartphones'") String category) {
        log.info("MCP Tool called: getProductsByCategory(category='{}')", category);
        try {
            List<ProductDto> products = productRestClient.get()
                    .uri("/api/products/category/{category}", category)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<ProductDto>>() {});
            return objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(products);
        } catch (Exception e) {
            log.error("Error in getProductsByCategory(category='{}')", category, e);
            return "Error fetching products by category: " + e.getMessage();
        }
    }

    @Tool(description = """
            Get all available product categories in the store.
            Use this when the user asks what categories exist.
            """)
    public String getAllCategories() {
        log.info("MCP Tool called: getAllCategories");
        try {
            List<String> categories = productRestClient.get()
                    .uri("/api/products/categories")
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<String>>() {});
            return objectMapper.writeValueAsString(categories);
        } catch (Exception e) {
            log.error("Error in getAllCategories", e);
            return "Error fetching categories: " + e.getMessage();
        }
    }

    @Tool(description = """
            Create a new product in the store.
            Returns the created product with its assigned ID.
            Use this when the user wants to add a new product.
            """)
    public String createProduct(
            @ToolParam(description = "The product name") String name,
            @ToolParam(description = "A description of the product") String description,
            @ToolParam(description = "The product price as a decimal number") double price,
            @ToolParam(description = "The product category") String category,
            @ToolParam(description = "How many units are in stock") int stockQuantity) {
        log.info("MCP Tool called: createProduct(name='{}')", name);
        try {
            ProductDto newProduct = new ProductDto(
                    null, name, description,
                    java.math.BigDecimal.valueOf(price),
                    category, stockQuantity);

            ProductDto created = productRestClient.post()
                    .uri("/api/products")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .body(newProduct)
                    .retrieve()
                    .body(ProductDto.class);

            return objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(created);
        } catch (Exception e) {
            log.error("Error in createProduct", e);
            return "Error creating product: " + e.getMessage();
        }
    }
}