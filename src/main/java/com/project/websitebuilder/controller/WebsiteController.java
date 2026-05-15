package com.project.websitebuilder.controller;

import com.project.websitebuilder.dto.GenerateRequest;
import com.project.websitebuilder.dto.GenerateResponse;
import com.project.websitebuilder.dto.ProjectSummaryDTO;
import com.project.websitebuilder.service.WebsiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class WebsiteController {

    @Autowired
    private WebsiteService websiteService;

    // POST /api/generate — generate and save a new website
    @PostMapping("/generate")
    public ResponseEntity<GenerateResponse> generateWebsite(@RequestBody GenerateRequest request) {
        if (request.getPrompt() == null || request.getPrompt().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        try {
            GenerateResponse response = websiteService.generateWebsite(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // GET /api/projects — return all projects (summary only, no code)
    @GetMapping("/projects")
    public ResponseEntity<List<ProjectSummaryDTO>> getHistory() {
        return ResponseEntity.ok(websiteService.getHistory());
    }

    // GET /api/projects/{id} — return full code for one project
    @GetMapping("/projects/{id}")
    public ResponseEntity<GenerateResponse> getProject(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(websiteService.getProjectById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

}