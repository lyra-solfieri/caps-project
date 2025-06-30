package com.lyra.caps.dto;

import com.lyra.caps.model.enums.OrigemPaciente;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AtendimentoDto {
    private Long id;
    private LocalDate data;
    private LocalDate dataDeConclusao;
    private OrigemPaciente origemPaciente;
    private LocalTime hora;
    private String observacao;
    private String encaminhamento;
    private Long numeroDaAutorizacao;
    private Long pacienteId;
}
