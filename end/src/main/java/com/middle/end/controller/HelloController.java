package com.middle.end.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Hello", description = "Simple greeting endpoint")
public class HelloController {

    @Operation(
            summary = "Get a greeting message",
            description = "Returns a simple 'Hello, World!' greeting message"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved greeting message",
                    content = @Content(
                            mediaType = "text/plain",
                            schema = @Schema(implementation = String.class, example = "Hello, World!")
                    )
            )
    })
    @GetMapping("/hello")
    public String hello() {
        return "Hello, World!";
    }
}
