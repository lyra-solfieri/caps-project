package com.lyra.caps.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lyra.caps.dto.AuthenticationDto;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @PostMapping("/login")
    public ResponseEntity login(@ResquestBody AuthenticationDto authenticationDto) {
        var userEmailPassword = new UsernamePasswordAuthenticationToken(authenticationDto.email(), authenticationDto.password());

        return ResponseEntity.ok().build();
    }

}
