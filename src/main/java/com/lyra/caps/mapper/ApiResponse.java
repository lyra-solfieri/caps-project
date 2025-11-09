package com.lyra.caps.mapper;

import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private int status;
    private String mensagem;        
    private LocalDateTime timestamp;  
    private T dados;                 

    
    public static <T> ApiResponse<T> sucesso(String mensagem, HttpStatus status, T dados) {
        return new ApiResponse<>(status.value(), mensagem, LocalDateTime.now(), dados);
    }

    public static ApiResponse<Object> erro(String mensagem, HttpStatus status) {
        return new ApiResponse<>(status.value(), mensagem, LocalDateTime.now(), null);
    }
}
