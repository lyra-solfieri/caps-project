package com.lyra.caps.dto;

import java.util.List;

import lombok.Data;

@Data
public class RaasRequestDTO {

    private Long pacienteId;
    private Long atendimentoId;
    private String nomeEstabelecimento;
    private String cnes;
    private List<RaasAcaoDto> acoes;

}
