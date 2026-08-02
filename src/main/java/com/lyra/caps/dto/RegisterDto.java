package com.lyra.caps.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import com.lyra.caps.model.enums.Role;

public record RegisterDto(
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    String email,

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 8, max = 100, message = "Senha deve ter entre 8 e 100 caracteres")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$",
        message = "Senha deve conter letra maiúscula, minúscula, número e caractere especial"
    )
    String password,

    @NotBlank(message = "Nome é obrigatório")
    String nome,

    @NotBlank(message = "Profissão é obrigatória")
    String profissao,

    Role role
) {

}
