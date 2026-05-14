package com.project.websitebuilder.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenerateResponse {

    private Long id;            // ← NEW: DB id of the saved project
    private String html;
    private String css;
    private String js;
    private String providerUsed; // ← NEW: which AI generated this

}