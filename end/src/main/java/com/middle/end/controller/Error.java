package com.middle.end.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Hidden
public class Error implements ErrorController {

    @GetMapping("/error")
    public String error() {
        return "404 or something, idk.";
    }
}
