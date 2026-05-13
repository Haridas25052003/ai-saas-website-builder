package com.project.websitebuilder.service.provider;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class GroqProvider implements AiProvider {

    @Value("${ai.groq.api-key}")
    private String apiKey;

    @Value("${ai.groq.model}")
    private String model;

    @Value("${ai.groq.url}")
    private String url;

    @Override
    public String getName() {
        return "Groq";
    }

    @Override
    public String generate(String prompt) throws Exception {
        // Groq uses the exact same API format as OpenAI
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
            throw new Exception("Groq returned status: " + response.statusCode());
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