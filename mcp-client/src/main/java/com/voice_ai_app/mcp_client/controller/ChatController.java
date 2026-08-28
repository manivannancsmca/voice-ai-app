package com.voice_ai_app.mcp_client.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.voice_ai_app.mcp_client.service.AiChatService;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private static final Logger log = LoggerFactory.getLogger(ChatController.class);
    private final AiChatService aiChatService;

    public ChatController(AiChatService aiChatService) {
        this.aiChatService = aiChatService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> chat(@RequestBody Map<String, String> request) {
        String message = request.get("message");

        if (message == null || message.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Message cannot be empty"));
        }

        try {
            String response = aiChatService.chat(message);
            return ResponseEntity.ok(Map.of("response", response));
        } catch (Exception e) {
            log.error("Error processing chat message", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to process message: " + e.getMessage()));
        }
    }
}
