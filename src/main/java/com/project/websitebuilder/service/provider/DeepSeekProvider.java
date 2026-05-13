package com.project.websitebuilder.service.provider;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class DeepSeekProvider implements AiProvider {

    @Value("${ai.deepseek.api-key}")
    private String apiKey;

    @Value("${ai.deepseek.model}")
    private String model;

    @Value("${ai.deepseek.url}")
    private String url;

    @Override
    public String getName() {
        return "DeepSeek";
    }

    @Override
    public String generate(String prompt) throws Exception {
        // DeepSeek is also OpenAI-compatible
        String body = """
                {
                  "model": "%s",
                  "messages": [
                    {
                      "role": "system",
                      "content": "You are an expert web developer. Generate complete websites. Always respond with ONLY a JSON object in this exact format, no explanation, no markdown: {\\"html\\": \\"...\\", \\"css\\": \\"...\\", \\"js\\": \\"...\\"}"
                    },
                    {
                      "role": "user",
                      "content": "%s"
                    }
                  ],
                  "temperature": 0.7
                }
                """.formatted(model, escapeJson(prompt));

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new Exception("DeepSeek returned status: " + response.statusCode());
        }

        return response.body();
    }

    private String escapeJson(String text) {
        return text.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
}