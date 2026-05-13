package com.project.websitebuilder.service.provider;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class GeminiProvider implements AiProvider {

    @Value("${ai.gemini.api-key}")
    private String apiKey;

    @Value("${ai.gemini.url}")
    private String url;

    @Override
    public String getName() {
        return "Gemini";
    }

    @Override
    public String generate(String prompt) throws Exception {
        String systemInstruction = "You are an expert web developer. Generate complete websites. " +
            "Always respond with ONLY a JSON object in this exact format, no explanation, no markdown: " +
            "{\\\"html\\\": \\\"...\\\", \\\"css\\\": \\\"...\\\", \\\"js\\\": \\\"...\\\"}";

        // Gemini has a different request body structure
        String body = """
                {
                  "system_instruction": {
                    "parts": [{ "text": "%s" }]
                  },
                  "contents": [{
                    "parts": [{ "text": "%s" }]
                  }],
                  "generationConfig": {
                    "temperature": 0.7
                  }
                }
                """.formatted(systemInstruction, escapeJson(prompt));

        // Gemini uses API key as a query parameter, not in the header
        String fullUrl = url + "?key=" + apiKey;

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(fullUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new Exception("Gemini returned status: " + response.statusCode());
        }

        // Gemini wraps its text in a different structure
        // We return raw body — AiProviderRouter will parse it
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