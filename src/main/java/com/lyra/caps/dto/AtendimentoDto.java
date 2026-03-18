package com.lyra.caps.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class AtendimentoDto {

    private Long id;
    private LocalDate dataAdmissao;
    private LocalDate dataConclusao;
    private String mesAtendimento;

    private Long numeroAutorizacao;

    private String usoDrogas;

    private boolean alcool;
    private boolean crack;
    private boolean outrasDrogas;

    private boolean origemDemandaEspontanea;
    private boolean origemAtencaoBasica;
    private boolean origemUrgencia;
    private boolean origemCaps;
    private boolean origemHospitalGeral;
    private boolean origemHospitalPsiquiatrico;

    private String cidPrincipal;
    private String descricaoCidPrincipal;

    private String cidSecundario;
    private String descricaoCidSecundario;

    private String esf;
    private String cnesAtendimento;

    private boolean encaminhamentoCaps;
    private boolean encaminhamentoAtencaoBasica;
    private boolean alta;
    private boolean obito;

    private String observacao;

    private Long pacienteId;
}