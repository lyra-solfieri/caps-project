package com.lyra.caps.dto;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EnderecoDto {

    
    @NotEmpty
    private String cep;

    private String logradouro;

    @Size(min = 1, max = 100)
    private String numero;
    private String complemento;
    private String bairro;

    @NotEmpty
    private String cidade;

    private String estado;
    private String pais;

}
