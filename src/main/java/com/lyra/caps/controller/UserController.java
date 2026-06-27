package com.lyra.caps.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lyra.caps.dto.UserDto;
import com.lyra.caps.service.IUserService;

@RestController
@RequestMapping("/api/usuarios")
public class UserController {

    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> listarUsuarios() {
        var usuarios = userService.getAllUsers().stream()
                .map(UserDto::fromEntity)
                .toList();
        return ResponseEntity.ok(usuarios);
    }

}
