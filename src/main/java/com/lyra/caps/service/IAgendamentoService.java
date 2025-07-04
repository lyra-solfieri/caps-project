package com.lyra.caps.service;

import com.lyra.caps.dto.AgendamentoRequestDto;
import com.lyra.caps.dto.AgendamentoResponseDto;

public interface IAgendamentoService {

    public AgendamentoResponseDto agendar(AgendamentoRequestDto agendamentoRequest);

}
