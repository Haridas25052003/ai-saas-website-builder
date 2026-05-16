package com.project.websitebuilder.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.websitebuilder.dto.GenerateResponse;
import org.springframework.stereotype.Service;

@Service
public class ResponseParserService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public GenerateResponse parse(String rawResponse) throws Exception {

        JsonNode root = objectMapper.readTree(rawResponse);

        String content;

        // ── OpenAI / Groq / DeepSeek format ──────────────────────────────
        if (root.has("choices")) {
            content = root
                    .path("choices").get(0)
                    .path("message")
                    .path("content")
                    .asText();
        }

        // ── Gemini format ─────────────────────────────────────────────────
        else if (root.has("candidates")) {
            content = root
                    .path("candidates").get(0)
                    .path("content")
                    .path("parts").get(0)
                    .path("text")
                    .asText();
        }

        else {
            throw new Exception("Unknown AI response format");
        }

        content = cleanContent(content);

        JsonNode parsed = objectMapper.readTree(content);

        // ✅ Use setters instead of constructor
        // id and providerUsed will be set later in WebsiteService
        GenerateResponse response = new GenerateResponse();
        response.setHtml(parsed.path("html").asText());
        response.setCss(parsed.path("css").asText());
        response.setJs(parsed.path("js").asText());
        return response;
    }

    private String cleanContent(String content) {
        content = content.trim();
        if (content.startsWith("```json")) {
            content = content.substring(7);
        } else if (content.startsWith("```")) {
            content = content.substring(3);
        }
        if (content.endsWith("```")) {
            content = content.substring(0, content.length() - 3);
        }
        return content.trim();
    }
}