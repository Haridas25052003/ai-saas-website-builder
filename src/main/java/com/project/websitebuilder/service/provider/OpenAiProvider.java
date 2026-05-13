package com.project.websitebuilder.service.provider;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

// @Component tells Spring to manage this class as a bean
@Component
public class OpenAiProvider implements AiProvider {

    // Reads values from application.properties
    @Value("${ai.openai.api-key}")
    private String apiKey;

    @Value("${ai.openai.model}")
    private String model;

    @Value("${ai.openai.url}")
    private String url;

    @Override
    public String getName() {
        return "OpenAI";
    }

    @Override
    public String generate(String prompt) throws Exception {
        // Build the JSON body manually — clean and no extra dependencies
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
            throw new Exception("OpenAI returned status: " + response.statusCode());
        }

        return response.body();
    }

    // Escape special characters so the prompt doesn't break the JSON body
    private String escapeJson(String text) {
        return text.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
}