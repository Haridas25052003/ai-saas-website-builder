package com.project.websitebuilder.controller;

import com.project.websitebuilder.dto.GenerateRequest;
import com.project.websitebuilder.dto.GenerateResponse;
import com.project.websitebuilder.service.WebsiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// @RestController = @Controller + @ResponseBody
// Means: this class handles HTTP requests, and return values become JSON automatically
@RestController

// All endpoints in this class start with /api
@RequestMapping("/api")

// Allows frontend (running on same or different origin) to call this API
// Without this, browsers block the request with a CORS error
@CrossOrigin(origins = "*")
public class WebsiteController {

    // Spring automatically injects our WebsiteService here
    // We don't create it with 'new' — Spring manages it
    @Autowired
    private WebsiteService websiteService;

    // This method handles: POST /api/generate
    // @RequestBody tells Spring: read the JSON body and convert it into a GenerateRequest object
    // ResponseEntity lets us control the HTTP status code (200 OK, 400 Bad Request, etc.)
    @PostMapping("/generate")
    public ResponseEntity<GenerateResponse> generateWebsite(@RequestBody GenerateRequest request) throws Exception {

        // Basic validation — don't process empty prompts
        if (request.getPrompt() == null || request.getPrompt().trim().isEmpty()) {
            return ResponseEntity.badRequest().build(); // returns HTTP 400
        }

        // Delegate to the service layer — controller should never contain business logic
        GenerateResponse response = websiteService.generateWebsite(request);

        // Return the response with HTTP 200 OK
        return ResponseEntity.ok(response);
    }

}