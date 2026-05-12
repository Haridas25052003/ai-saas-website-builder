package com.project.websitebuilder.service;

import com.project.websitebuilder.dto.GenerateRequest;
import com.project.websitebuilder.dto.GenerateResponse;
import org.springframework.stereotype.Service;

// @Service tells Spring: "this is a business logic class, manage it for me"
// Spring will create one instance of this and inject it wherever needed
@Service
public class WebsiteService {

    // This method takes the user's prompt and returns generated code
    // For now we return DUMMY data — we'll replace this with OpenAI in Step 3
    public GenerateResponse generateWebsite(GenerateRequest request) {

        // Just so we can see the prompt arrived correctly
        System.out.println("Received prompt: " + request.getPrompt());

        // --- DUMMY RESPONSE (will be replaced by OpenAI call in Step 3) ---
        String html = """
                <section class="hero">
                  <h1>Welcome to My Portfolio</h1>
                  <p>Java Developer | Problem Solver | Builder</p>
                  <a href="#projects" class="btn">View My Work</a>
                </section>
                <section id="projects">
                  <h2>Projects</h2>
                  <div class="card">Spring Boot API</div>
                  <div class="card">AI Website Builder</div>
                </section>
                """;

        String css = """
                * { margin: 0; padding: 0; box-sizing: border-box; }
                body { font-family: 'Segoe UI', sans-serif; background: #0d0d0d; color: #f0f0f0; }
                .hero { text-align: center; padding: 80px 20px; background: linear-gradient(135deg, #1a1a2e, #16213e); }
                .hero h1 { font-size: 2.8rem; margin-bottom: 16px; color: #00d4ff; }
                .hero p  { font-size: 1.2rem; color: #aaa; margin-bottom: 32px; }
                .btn { padding: 12px 32px; background: #00d4ff; color: #000; border-radius: 6px;
                       text-decoration: none; font-weight: bold; transition: background 0.3s; }
                .btn:hover { background: #00a8cc; }
                #projects { padding: 60px 40px; }
                #projects h2 { text-align: center; margin-bottom: 32px; font-size: 2rem; }
                .card { background: #1e1e2e; padding: 24px; margin: 16px auto;
                        max-width: 500px; border-radius: 10px; border: 1px solid #333; }
                """;

        String js = """
                console.log('Portfolio website loaded!');
                document.querySelector('.btn').addEventListener('click', function(e) {
                  e.preventDefault();
                  document.querySelector('#projects').scrollIntoView({ behavior: 'smooth' });
                });
                """;

        // Build and return the response object
        // Spring automatically converts this to JSON: { "html": "...", "css": "...", "js": "..." }
        return new GenerateResponse(html, css, js);
    }

}