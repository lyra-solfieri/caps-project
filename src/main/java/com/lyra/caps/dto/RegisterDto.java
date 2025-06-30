package com.lyra.caps.dto;

import com.lyra.caps.model.enums.Role;

public record RegisterDto(String email, String password, Role role) {

}
