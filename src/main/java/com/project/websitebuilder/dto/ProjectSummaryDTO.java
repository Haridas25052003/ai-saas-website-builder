package com.project.websitebuilder.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// Lightweight version of GeneratedProject
// Used for the history list — no code fields, just metadata
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectSummaryDTO {

    private Long id;
    private String prompt;
    private String providerUsed;
    private String status;
    private LocalDateTime createdAt;

}