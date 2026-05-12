package com.project.websitebuilder.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// @Data          → getters + setters
// @AllArgsConstructor → constructor with all fields (we'll use this to build the response)
// @NoArgsConstructor  → empty constructor (required by Spring/Jackson for JSON conversion)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenerateResponse {

    // These three fields become the JSON keys in our response:
    // { "html": "...", "css": "...", "js": "..." }
    private String html;
    private String css;
    private String js;

}