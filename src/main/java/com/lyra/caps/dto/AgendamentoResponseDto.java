package com.lyra.caps.dto;

import java.time.LocalDateTime;

import com.lyra.caps.model.enums.StatusAgendamento;
import com.lyra.caps.model.enums.TipoComunicacao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AgendamentoResponseDto {

    String destinatario;
    String message;
    TipoComunicacao tipoComunicacao;
    LocalDateTime dataHoraAtendimento;
    StatusAgendamento statusAgendamento;
    LocalDateTime createAt;
    PacienteDto paciente;
    UserDto user;
}