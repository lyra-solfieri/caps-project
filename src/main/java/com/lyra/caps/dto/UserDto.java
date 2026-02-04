package com.lyra.caps.dto;

import com.lyra.caps.model.User;
import com.lyra.caps.model.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;  
    private String nome;
    private String email;
    private Role role;
    
    public static UserDto fromEntity(User user) {
        if (user == null) {
            return null;
        }
        return new UserDto(user.getId(), user.getNome(), user.getEmail(), user.getRole());
    }
}