package com.project.websitebuilder.service.provider;

// This is a contract — every AI provider MUST implement this method
// The router only knows about this interface, not the specific providers
// This is the "Open/Closed" principle — add new providers without changing existing code
public interface AiProvider {

    // Returns the name of this provider (used for logging)
    String getName();

    // Takes a user prompt and returns the raw response from the AI
    // Throws an exception if the call fails (so the router can try the next provider)
    String generate(String prompt) throws Exception;

}