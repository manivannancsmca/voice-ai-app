package com.voice_ai_app.mcp_client.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

/**
 * Service that wraps the ChatClient and provides a simple chat method.
 *
 * The flow:
 *   1. User message arrives
 *   2. ChatClient sends it to Ollama (Llama 3) along with tool definitions
 *   3. Llama 3 either responds directly or requests a tool call
 *   4. If tool call: ChatClient sends the request to the MCP Server,
 *      gets the result, and sends it back to Llama 3
 *   5. Llama 3 generates the final natural language response
 *   6. Response is returned to the caller
 */
@Service
public class AiChatService {

    private static final Logger log = LoggerFactory.getLogger(AiChatService.class);

    private final ChatClient chatClient;

    public AiChatService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String chat(String userMessage) {
        log.info("Processing user message: {}", userMessage);

        String response = chatClient.prompt()
                .user(userMessage)
                .call()
                .content();

        log.info("AI response: {}", response);
        return response;
    }
}