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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/api/usuarios")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "Listar usuários", description = "Lista todos os usuários do sistema. Requer perfil ADMIN.")
    @ApiResponse(responseCode = "200", description = "Lista de usuários retornada")
    @ApiResponse(responseCode = "403", description = "Acesso negado")
    @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
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
