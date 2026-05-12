package com.project.websitebuilder.dto;

import lombok.Data;

// @Data is a Lombok annotation — it automatically generates:
// getters, setters, toString, equals, hashCode
// So we don't have to write all that boilerplate manually
@Data
public class GenerateRequest {

    // This field maps to the "prompt" key in the incoming JSON
    // { "prompt": "Create a dark portfolio website" }
    private String prompt;

}