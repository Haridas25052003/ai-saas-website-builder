package com.project.websitebuilder.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.websitebuilder.dto.GenerateResponse;
import org.springframework.stereotype.Service;

@Service
public class ResponseParserService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Parses raw AI response → GenerateResponse
    // Handles both OpenAI/Groq/DeepSeek format AND Gemini format
    public GenerateResponse parse(String rawResponse) throws Exception {

        JsonNode root = objectMapper.readTree(rawResponse);

        String content;

        // ── OpenAI / Groq / DeepSeek format ──────────────────────────────
        // { "choices": [{ "message": { "content": "{...}" } }] }
        if (root.has("choices")) {
            content = root
                .path("choices").get(0)
                .path("message")
                .path("content")
                .asText();
        }

        // ── Gemini format ─────────────────────────────────────────────────
        // { "candidates": [{ "content": { "parts": [{ "text": "{...}" }] } }] }
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

        // The content itself is a JSON string like:
        // { "html": "...", "css": "...", "js": "..." }
        // Strip markdown code fences if the AI wrapped it anyway
        content = cleanContent(content);

        JsonNode parsed = objectMapper.readTree(content);

        return new GenerateResponse(
            parsed.path("html").asText(),
            parsed.path("css").asText(),
            parsed.path("js").asText()
        );
    }

    // Sometimes AI wraps JSON in ```json ... ``` even when told not to
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