package com.middle.end.controller;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Hidden
public class Error implements ErrorController {

    @RequestMapping("/error")
    public ResponseEntity<String> error(HttpServletRequest request) {
        Object code = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        HttpStatus status = code instanceof Integer value
                ? HttpStatus.valueOf(value)
                : HttpStatus.INTERNAL_SERVER_ERROR;

        // Keep the plain-text response, but do not dress a 400 up as a 200.
        return ResponseEntity.status(status).body(status.value() + " " + status.getReasonPhrase());
    }
}
