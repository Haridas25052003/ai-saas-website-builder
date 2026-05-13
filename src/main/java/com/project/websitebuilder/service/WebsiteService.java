package com.project.websitebuilder.service;

import com.project.websitebuilder.dto.GenerateRequest;
import com.project.websitebuilder.dto.GenerateResponse;
import org.springframework.stereotype.Service;

@Service
public class WebsiteService {

    private final AiProviderRouter router;
    private final ResponseParserService parser;

    public WebsiteService(AiProviderRouter router, ResponseParserService parser) {
        this.router = router;
        this.parser = parser;
    }

    public GenerateResponse generateWebsite(GenerateRequest request) throws Exception {

        // Build a detailed prompt — prompt engineering matters a lot here
        String enrichedPrompt = """
                Generate a complete, modern, responsive website based on this description:
                "%s"
                
                Requirements:
                - Use semantic HTML5
                - Modern CSS with animations and hover effects
                - Clean JavaScript with no external dependencies
                - Bootstrap 5 classes are allowed in HTML
                - Make it visually impressive and production-ready
                - Mobile responsive design
                
                Return ONLY this JSON, no explanation:
                {"html": "...", "css": "...", "js": "..."}
                """.formatted(request.getPrompt());

        // Router picks the provider, handles fallback automatically
        String rawResponse = router.route(enrichedPrompt);

        // Parse whichever format came back
        return parser.parse(rawResponse);
    }
}