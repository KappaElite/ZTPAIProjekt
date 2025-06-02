package com.example.ztpai.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Tag(name = "Health Check", description = "API health check endpoints")
public class HelloController {

    @GetMapping("/hello")
    @Operation(summary = "Health check endpoint", description = "Simple endpoint to verify API is running")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "API is up and running",
            content = @Content(mediaType = "text/plain", schema = @Schema(type = "string")))
    })
    public String sayHello() {
        return "Hello, World!";
    }
}
