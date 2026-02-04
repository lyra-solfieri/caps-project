package com.lyra.caps.dto;


import com.lyra.caps.model.Endereco;

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

    public static EnderecoDto fromEntity(Endereco endereco) {
        if (endereco == null) {
            return null;
        }

        EnderecoDto dto = new EnderecoDto();
        dto.setCep(endereco.getCep());
        dto.setLogradouro(endereco.getLogradouro());
        dto.setNumero(endereco.getNumero());
        dto.setComplemento(endereco.getComplemento());
        dto.setBairro(endereco.getBairro());
        dto.setCidade(endereco.getCidade());
        dto.setEstado(endereco.getEstado());
        dto.setPais(endereco.getPais());
        return dto;
    }
}
