package com.voice_ai_app.mcp_server.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public record ProductDto(
        Long id,
        String name,
        String description,
        BigDecimal price,
        String category,
        @JsonProperty("stock_quantity") Integer stockQuantity
        
) {}
