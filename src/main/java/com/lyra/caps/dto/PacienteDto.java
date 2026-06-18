package com.lyra.caps.dto;

import java.time.LocalDate;

import org.hibernate.validator.constraints.br.CPF;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    @Email(message = "Email inválido")
    @NotEmpty
    private String email;

    private String telefone;

    @CPF(message = "CPF inválido")
    @NotEmpty
    private String cpf;

    private EnderecoDto endereco;

    private Genero genero;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataNascimento;

    private String nomeMae;
    private String nomePai;
    private String profissao;

    private Long numeroCartaoSUS;
    private Raca raca;
    private Long numeroProntuario;

    private boolean infantojuvenil;
    private boolean emSituacaoSocioeconomica;

    private String codIBGEMunicipio;
    private String etniaIndigena;
    private String nacionalidade;
    private String escolaridade;
    private String rg;

    private String profissaoMae;
    private String profissaoPai;
    private String rendaFamiliar;

    private String auxilioOuBeneficioSocial;

    private String responsavelLegal;
    private String parentescoResponsavelLegal;
    private String telefoneResponsavelLegal;
    private String profissaoResponsavelLegal;

    private String instituicaoEnsino;
    private String religiao;
    private String unidadeBasicaSaude;
    private String agenteSaude;

    private String contextoVulnerabilidadeSocioeconomica;
}
