package com.shruti.easypay_agent.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

@Component
public class AnthropicClient {

    @Value("${anthropic.api.key}")
    private String apiKey;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String call(String systemPrompt, String userPrompt,
                       List<Map<String, Object>> tools) throws Exception {

        Map<String, Object> body = Map.of(
                "model", "claude-sonnet-4-6",
                "max_tokens", 1000,
                "system", systemPrompt,
                "tools", tools,
                "messages", List.of(Map.of("role", "user", "content", userPrompt))
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.anthropic.com/v1/messages"))
                .header("Content-Type", "application/json")
                .header("x-api-key", apiKey)
                .header("anthropic-version", "2023-06-01")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)))
                .build();

        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        return response.body();
    }

    public String callWithToolResult(String systemPrompt, String userPrompt,
                                     List<Map<String, Object>> tools,
                                     String toolUseId, String toolName,
                                     Map<String, Object> toolInput,
                                     String toolResult) throws Exception {

        List<Map<String, Object>> messages = List.of(
                Map.of("role", "user", "content", userPrompt),
                Map.of("role", "assistant", "content", List.of(Map.of(
                        "type", "tool_use",
                        "id", toolUseId,
                        "name", toolName,
                        "input", toolInput
                ))),
                Map.of("role", "user", "content", List.of(Map.of(
                        "type", "tool_result",
                        "tool_use_id", toolUseId,
                        "content", toolResult
                )))
        );

        Map<String, Object> body = Map.of(
                "model", "claude-sonnet-4-6",
                "max_tokens", 1000,
                "system", systemPrompt,
                "tools", tools,
                "messages", messages
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.anthropic.com/v1/messages"))
                .header("Content-Type", "application/json")
                .header("x-api-key", apiKey)
                .header("anthropic-version", "2023-06-01")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)))
                .build();

        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        return response.body();
    }
}