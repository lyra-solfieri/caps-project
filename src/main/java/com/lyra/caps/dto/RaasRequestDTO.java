package com.lyra.caps.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class RaasRequestDTO {

    private Long pacienteId;
    private Long atendimentoId;
    private String nomeEstabelecimento;
    private String cnes;
    private List<RaasAcaoDto> acoes;

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
}
