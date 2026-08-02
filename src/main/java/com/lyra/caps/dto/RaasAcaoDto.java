package com.lyra.caps.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class RaasAcaoDto {

    private String codigoAcao;
    private Integer quantidade;
    private LocalDate data;
    private String cid10;
    private String servico;
    private String classificacao;
    private String cboExecutor;
    private String cartaoProfissional;
    private String codigoEquipe;

    private boolean caps;
    private boolean territorio;
}