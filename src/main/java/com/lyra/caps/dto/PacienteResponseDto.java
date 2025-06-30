package com.lyra.caps.dto;

import com.lyra.caps.model.enums.Genero;
import com.lyra.caps.model.enums.Raca;

import lombok.Data;


@Data
public class PacienteResponseDto {

    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private String cpf;
    private EnderecoDto endereco;
    private Genero genero;
    private String dataNascimento;
    private String nomeMae;
    private String nomePai;
    private String profissao;
    private String numeroCartaoSUS;
    private Raca raca;
    private Long numeroProntuario;
    private boolean infantojuvenil;
    private boolean emSituacaoSocioeconomica;

}
