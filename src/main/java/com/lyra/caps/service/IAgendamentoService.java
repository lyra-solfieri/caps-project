package com.lyra.caps.service;
import java.util.List;
import com.lyra.caps.dto.AgendamentoRequestDto;
import com.lyra.caps.dto.AgendamentoResponseDto;

public interface IAgendamentoService {

    public AgendamentoResponseDto agendar(AgendamentoRequestDto agendamentoRequest);

    public AgendamentoResponseDto buscarPorId(Long id);
    
    public void deletarAgendamento(Long id);
    
    public String alertarAgendamento(Long id);

    public List<AgendamentoResponseDto> listarAgendamentos();

    public AgendamentoResponseDto atualizarAgendamento(Long id, AgendamentoRequestDto agendamentoRequest);

}
