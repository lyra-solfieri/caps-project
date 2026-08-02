package com.lyra.caps.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class RaasAceitarDTO {

    private String correlationId;

    // Estabelecimento
    private String nomeEstabelecimento;
    private String cnes;

    // Paciente
    private String numeroProntuario;
    private String nomePaciente;
    private String cartaoSus;
    private String sexo;
    private LocalDate dataNascimento;
    private String nomeMae;
    private String nacionalidade;
    private String racaCor;
    private String etniaIndigena;
    private String nomeResponsavel;
    private String municipio;
    private String uf;
    private String codigoIbge;
    private String cep;
    private String endereco;
    private String complemento;
    private String telefoneCelular;
    private String telefoneContato;

    // Atendimento
    private LocalDate dataAdmissao;
    private LocalDate dataConclusao;
    private String mesAtendimento;
    private Long numeroAutorizacao;

    private String usoDrogas;
    private boolean alcool;
    private boolean crack;
    private boolean outrasDrogas;

    private boolean origemDemanda;
    private boolean origemAtencaoBasica;
    private boolean origemUrgencia;
    private boolean origemCaps;
    private boolean origemHospital;
    private boolean origemPsiquiatrico;

    private String cidPrincipal;
    private String descricaoCidPrincipal;
    private String cidSecundario;
    private String descricaoCidSecundario;

    private String esf;
    private String cnesAtendimento;

    private boolean encaminhamentoCaps;
    private boolean encaminhamentoAB;
    private boolean alta;
    private boolean obito;

    private List<RaasAcaoDto> acoes;
}
