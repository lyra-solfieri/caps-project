package com.lyra.caps.dto;

import org.hibernate.validator.constraints.br.CPF;

import com.lyra.caps.model.enums.Genero;
import com.lyra.caps.model.enums.Raca;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PacienteDto {
    private Long id;

    @NotEmpty
    @Size(min = 3, max = 100)
    private String nome;

    @Email(message = "Email inválido")
    @NotEmpty
    private String email;

    private String telefone;

    @CPF(message = "CPF inválido")
    @NotEmpty
    private String cpf;

    private EnderecoDto endereco;
    private Genero genero;
    private String dataNascimento;
    private String nomeMae;
    private String nomePai;
    private String profissao;
    private Long numeroCartaoSUS;
    private Raca raca;
    private Long numeroProntuario;
    private boolean infantojuvenil;
    private boolean emSituacaoSocioeconomica;
}
