package com.lyra.caps.model.enums;

public enum Role {
    USER,          // Usuário comum
    ADMIN,         // Administrador com todos os privilégios
    MODERATOR,     // Moderador com privilégios limitados
    EDITOR,        // Editor de conteúdo (se aplicável)
    GUEST;         // Convidado (acesso limitado)

    // Você pode adicionar métodos úteis se necessário
    public String getAuthority() {
        return "ROLE_" + this.name();
    }
}