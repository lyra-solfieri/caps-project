package com.lyra.caps.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.lyra.caps.dto.relatorio.IndicadoresDesempenhoDto;
import com.lyra.caps.dto.relatorio.AtendimentoPorProfissionalDto;
import com.lyra.caps.model.enums.StatusAgendamento;
import com.lyra.caps.repository.AgendamentoRepository;
import com.lyra.caps.repository.AtendimentoRepository;
import com.lyra.caps.repository.PacienteRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class RelatorioService implements IRelatorioService {

    private final PacienteRepository pacienteRepository;
    private final AgendamentoRepository agendamentoRepository;
    private final AtendimentoRepository atendimentoRepository;

    @Override
    public IndicadoresDesempenhoDto obterIndicadores(String inicio, String fim) {
        LocalDate dataInicio = parseDate(inicio);
        LocalDate dataFim = parseDate(fim);
        LocalDateTime dataHoraInicio = dataInicio != null ? dataInicio.atStartOfDay() : null;
        LocalDateTime dataHoraFim = dataFim != null ? dataFim.atTime(23, 59, 59) : null;

        long totalPacientes = pacienteRepository.count();

        List<Object[]> agendamentosPorStatus = agendamentoRepository.countByStatusAndPeriodo(dataHoraInicio, dataHoraFim);
        long totalAgendamentos = 0;
        long realizados = 0;
        long cancelados = 0;
        long pendentes = 0;

        for (Object[] row : agendamentosPorStatus) {
            StatusAgendamento status = (StatusAgendamento) row[0];
            long count = (long) row[1];
            totalAgendamentos += count;
            switch (status) {
                case REALIZADO -> realizados += count;
                case CANCELADO -> cancelados += count;
                case PENDENTE, AGENDADO -> pendentes += count;
            }
        }

        long totalAtendimentos = atendimentoRepository.countByPeriodo(
                dataInicio, dataFim);

        double taxaComparecimento = totalAgendamentos > 0
                ? (double) realizados / totalAgendamentos * 100.0
                : 0.0;

        double mediaAtendimentosPorPaciente = totalPacientes > 0
                ? (double) totalAtendimentos / totalPacientes
                : 0.0;

        return new IndicadoresDesempenhoDto(
                totalPacientes,
                totalAgendamentos,
                realizados,
                cancelados,
                pendentes,
                totalAtendimentos,
                Math.round(taxaComparecimento * 100.0) / 100.0,
                Math.round(mediaAtendimentosPorPaciente * 100.0) / 100.0
        );
    }

    @Override
    public List<AtendimentoPorProfissionalDto> obterAtendimentosPorProfissional(String inicio, String fim) {
        LocalDate dataInicio = parseDate(inicio);
        LocalDate dataFim = parseDate(fim);

        List<Object[]> resultados = atendimentoRepository.countGroupedByMes(dataInicio, dataFim);
        List<AtendimentoPorProfissionalDto> lista = new ArrayList<>();

        for (Object[] row : resultados) {
            String mes = (String) row[0];
            long quantidade = (long) row[1];
            lista.add(new AtendimentoPorProfissionalDto(mes, quantidade));
        }

        return lista;
    }

    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) return null;
        return LocalDate.parse(dateStr, DateTimeFormatter.ISO_DATE);
    }
}
