package com.project.websitebuilder.service;

import com.project.websitebuilder.service.provider.AiProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class AiProviderRouter {

    private final List<AiProvider> providers;
    private final AtomicInteger roundRobinIndex = new AtomicInteger(0);

    // Stores the name of the provider that last succeeded
    private String lastUsedProvider = "Unknown";

    @Value("${ai.strategy:ROUND_ROBIN}")
    private String strategy;

    public AiProviderRouter(List<AiProvider> providers) {
        this.providers = providers;
        System.out.println("Loaded AI providers: " +
                providers.stream().map(AiProvider::getName).toList());
    }

    public String route(String prompt) throws Exception {
        if ("ROUND_ROBIN".equalsIgnoreCase(strategy)) {
            return roundRobin(prompt);
        } else {
            return fallback(prompt);
        }
    }

    // ← NEW: lets WebsiteService ask "who just responded?"
    public String getLastUsedProvider() {
        return lastUsedProvider;
    }

    private String roundRobin(String prompt) throws Exception {
        int startIndex = roundRobinIndex.getAndIncrement() % providers.size();

        for (int i = 0; i < providers.size(); i++) {
            int index = (startIndex + i) % providers.size();
            AiProvider provider = providers.get(index);
            try {
                System.out.println("[Router] Trying provider: " + provider.getName());
                String result = provider.generate(prompt);
                lastUsedProvider = provider.getName(); // ← track it
                System.out.println("[Router] Success with: " + provider.getName());
                return result;
            } catch (Exception e) {
                System.out.println("[Router] " + provider.getName() + " failed: " + e.getMessage());
            }
        }
        throw new Exception("All AI providers failed. Please try again later.");
    }

    private String fallback(String prompt) throws Exception {
        for (AiProvider provider : providers) {
            try {
                System.out.println("[Router] Trying provider: " + provider.getName());
                String result = provider.generate(prompt);
                lastUsedProvider = provider.getName(); // ← track it
                System.out.println("[Router] Success with: " + provider.getName());
                return result;
            } catch (Exception e) {
                System.out.println("[Router] " + provider.getName() + " failed: " + e.getMessage());
            }
        }
        throw new Exception("All AI providers failed. Please try again later.");
    }
}