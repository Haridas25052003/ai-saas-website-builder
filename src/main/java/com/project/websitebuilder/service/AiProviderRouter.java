package com.project.websitebuilder.service;

import com.project.websitebuilder.service.provider.AiProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class AiProviderRouter {

    // Spring automatically injects ALL beans that implement AiProvider
    // So this list will contain: OpenAiProvider, GroqProvider, GeminiProvider, DeepSeekProvider
    private final List<AiProvider> providers;

    // AtomicInteger is thread-safe — important for concurrent requests
    private final AtomicInteger roundRobinIndex = new AtomicInteger(0);

    @Value("${ai.strategy:ROUND_ROBIN}")
    private String strategy;

    // Spring injects the list automatically — no manual wiring needed
    public AiProviderRouter(List<AiProvider> providers) {
        this.providers = providers;
        System.out.println("Loaded AI providers: " +
                providers.stream().map(AiProvider::getName).toList());
    }

    // Main method — routes to the right provider based on strategy
    // Returns the raw response string from whichever provider succeeded
    public String route(String prompt) throws Exception {
        if ("ROUND_ROBIN".equalsIgnoreCase(strategy)) {
            return roundRobin(prompt);
        } else {
            return fallback(prompt);
        }
    }

    // Round-robin: each request goes to the next provider in line
    // Request 1 → OpenAI, Request 2 → Groq, Request 3 → Gemini, etc.
    private String roundRobin(String prompt) throws Exception {
        int startIndex = roundRobinIndex.getAndIncrement() % providers.size();

        // Try starting from the current index, then fall through to others
        for (int i = 0; i < providers.size(); i++) {
            int index = (startIndex + i) % providers.size();
            AiProvider provider = providers.get(index);
            try {
                System.out.println("[Router] Trying provider: " + provider.getName());
                String result = provider.generate(prompt);
                System.out.println("[Router] Success with: " + provider.getName());
                return result;
            } catch (Exception e) {
                System.out.println("[Router] " + provider.getName() + " failed: " + e.getMessage());
                // Continue to next provider
            }
        }
        throw new Exception("All AI providers failed. Please try again later.");
    }

    // Fallback: always tries in order (OpenAI first, then Groq, then Gemini, then DeepSeek)
    private String fallback(String prompt) throws Exception {
        for (AiProvider provider : providers) {
            try {
                System.out.println("[Router] Trying provider: " + provider.getName());
                String result = provider.generate(prompt);
                System.out.println("[Router] Success with: " + provider.getName());
                return result;
            } catch (Exception e) {
                System.out.println("[Router] " + provider.getName() + " failed: " + e.getMessage());
            }
        }
        throw new Exception("All AI providers failed. Please try again later.");
    }
}