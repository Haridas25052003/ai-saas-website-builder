package com.project.websitebuilder.service;

import com.project.websitebuilder.dto.GenerateRequest;
import com.project.websitebuilder.dto.GenerateResponse;
import com.project.websitebuilder.dto.ProjectSummaryDTO;
import com.project.websitebuilder.entity.GeneratedProject;
import com.project.websitebuilder.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WebsiteService {

    private final AiProviderRouter router;
    private final ResponseParserService parser;
    private final ProjectRepository projectRepository;

    public WebsiteService(AiProviderRouter router,
                          ResponseParserService parser,
                          ProjectRepository projectRepository) {
        this.router = router;
        this.parser = parser;
        this.projectRepository = projectRepository;
    }

    public GenerateResponse generateWebsite(GenerateRequest request) {

        // Build enriched prompt
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

        // We'll track which provider succeeded
        String providerUsed = "Unknown";
        GenerateResponse response;

        try {
            // Route to an AI provider
            // We need to know WHICH provider responded — so we check the router
            String rawResponse = router.route(enrichedPrompt);
            providerUsed = router.getLastUsedProvider(); // we'll add this method next

            // Parse the response
            response = parser.parse(rawResponse);
            response.setProviderUsed(providerUsed);

            // ── Save SUCCESS record to MySQL ──────────────────────────────
            GeneratedProject project = new GeneratedProject();
            project.setPrompt(request.getPrompt());
            project.setHtmlCode(response.getHtml());
            project.setCssCode(response.getCss());
            project.setJsCode(response.getJs());
            project.setProviderUsed(providerUsed);
            project.setStatus("SUCCESS");

            GeneratedProject saved = projectRepository.save(project);
            response.setId(saved.getId()); // attach DB id to response

        } catch (Exception e) {

            // ── Save FAILED record to MySQL ───────────────────────────────
            GeneratedProject failed = new GeneratedProject();
            failed.setPrompt(request.getPrompt());
            failed.setProviderUsed(providerUsed);
            failed.setStatus("FAILED");
            projectRepository.save(failed);

            throw new RuntimeException("Website generation failed: " + e.getMessage());
        }

        return response;
    }

    // Return history as lightweight summaries (no code content)
    public List<ProjectSummaryDTO> getHistory() {
        return projectRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(p -> new ProjectSummaryDTO(
                        p.getId(),
                        p.getPrompt(),
                        p.getProviderUsed(),
                        p.getStatus(),
                        p.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    // Return a single project's full code by ID
    public GenerateResponse getProjectById(Long id) {
        GeneratedProject project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + id));

        GenerateResponse response = new GenerateResponse();
        response.setId(project.getId());
        response.setHtml(project.getHtmlCode());
        response.setCss(project.getCssCode());
        response.setJs(project.getJsCode());
        response.setProviderUsed(project.getProviderUsed());
        return response;
    }
}