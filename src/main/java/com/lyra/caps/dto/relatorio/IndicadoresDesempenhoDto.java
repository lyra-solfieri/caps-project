package com.lyra.caps.dto.relatorio;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IndicadoresDesempenhoDto {
    private long totalPacientes;
    private long totalAgendamentosPeriodo;
    private long agendamentosRealizados;
    private long agendamentosCancelados;
    private long agendamentosPendentes;
    private long totalAtendimentos;
    private double taxaComparecimento;
    private double mediaAtendimentosPorPaciente;
}
