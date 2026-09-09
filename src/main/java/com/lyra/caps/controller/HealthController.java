package com.lyra.caps.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
public class HealthController {

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Endpoint público de verificação de disponibilidade da aplicação.")
    @ApiResponse(responseCode = "200", description = "Aplicação saudável")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP"));
    }
}
