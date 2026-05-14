package com.project.websitebuilder.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// @Entity tells JPA: this class represents a database table
// @Table lets us name the table exactly what we want
@Entity
@Table(name = "generated_projects")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeneratedProject {

    // Primary key — MySQL auto-increments this value (1, 2, 3, ...)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The prompt the user typed
    // @Column(columnDefinition = "TEXT") because prompts can be long
    @Column(name = "prompt", columnDefinition = "TEXT", nullable = false)
    private String prompt;

    // Generated HTML — can be very large so we use LONGTEXT
    @Column(name = "html_code", columnDefinition = "LONGTEXT")
    private String htmlCode;

    // Generated CSS
    @Column(name = "css_code", columnDefinition = "LONGTEXT")
    private String cssCode;

    // Generated JavaScript
    @Column(name = "js_code", columnDefinition = "LONGTEXT")
    private String jsCode;

    // Which AI provider generated this (OpenAI, Groq, Gemini, DeepSeek)
    @Column(name = "provider_used", length = 50)
    private String providerUsed;

    // Status: SUCCESS or FAILED
    @Column(name = "status", length = 20)
    private String status;

    // Automatically set when record is created
    // updatable = false means this value never changes after first insert
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // @PrePersist runs automatically BEFORE the record is saved to DB
    // Perfect for setting timestamps — we never forget to set it manually
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}