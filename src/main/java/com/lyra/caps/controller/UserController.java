package com.lyra.caps.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lyra.caps.dto.UserDto;
import com.lyra.caps.service.IUserService;
import com.lyra.caps.util.LogPersonalizado;

@RestController
@RequestMapping("/api/usuarios")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> listarUsuarios() {
        try {
            var usuarios = userService.getAllUsers().stream()
                    .map(UserDto::fromEntity)
                    .toList();
            return ResponseEntity.ok(usuarios);
        } catch (Exception e) {
            LogPersonalizado.getLogger(this.getClass()).error("Erro ao listar usuarios", e);
            return ResponseEntity.internalServerError().build();
        }
    }

}
