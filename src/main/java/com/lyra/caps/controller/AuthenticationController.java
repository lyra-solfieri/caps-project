package com.lyra.caps.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lyra.caps.dto.AuthenticationDto;
import com.lyra.caps.dto.LoginResponseDto;
import com.lyra.caps.dto.RegisterDto;
import com.lyra.caps.infra.security.TokenService;
import com.lyra.caps.model.User;
import com.lyra.caps.repository.UserRepository;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenService tokenService;
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody AuthenticationDto authentication) {

        var userEmailPassword = new UsernamePasswordAuthenticationToken(authentication.email(), authentication.password());
        var auth = this.authenticationManager.authenticate(userEmailPassword);

        var token = tokenService.generateToken((User) auth.getPrincipal());

        return ResponseEntity.ok(new LoginResponseDto(token));
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegisterDto register) {
        if(this.userRepository.findByEmail(register.email()) != null) {
            return ResponseEntity.badRequest().build();
        }

        String encryptedPassword = new BCryptPasswordEncoder().encode(register.password());
        User user = new User(register.email(), encryptedPassword, register.role());
        this.userRepository.save(user);

        return ResponseEntity.ok().build();
    }

}
