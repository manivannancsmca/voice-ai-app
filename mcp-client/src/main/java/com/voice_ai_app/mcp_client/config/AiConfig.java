package com.voice_ai_app.mcp_client.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    @Bean
    public ChatMemory chatMemory() {
        return MessageWindowChatMemory.builder()
                .maxMessages(20)
                .build();
    }

    @Bean
    public ChatClient chatClient(
            ChatClient.Builder builder,
            ToolCallbackProvider toolCallbackProvider,
            ChatMemory chatMemory) {

        return builder
                .defaultSystem("""
                        You are a helpful product store assistant.

                        You can help users:
                        - Browse and search for products
                        - Get details about specific products
                        - Filter products by category
                        - List available categories
                        - Create new products

                        Always use the available tools to fetch real product data.

                        Format your responses in a clear, readable way.
                        Be concise and friendly.
                        """)
                .defaultTools(toolCallbackProvider)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
                )
                .build();
    }
}
