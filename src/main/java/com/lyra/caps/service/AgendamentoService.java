package com.lyra.caps.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.lyra.caps.dto.AgendamentoRequestDto;
import com.lyra.caps.dto.AgendamentoResponseDto;
import com.lyra.caps.model.Agendamento;
import com.lyra.caps.model.enums.StatusAgendamento;
import com.lyra.caps.repository.AgendamentoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgendamentoService implements IAgendamentoService {

    private final AgendamentoRepository agendamentoRepository;

    public AgendamentoResponseDto agendar(AgendamentoRequestDto agendamentoRequest) {

        Agendamento agendamento = Agendamento.builder()
                .destinatario(agendamentoRequest.getDestinatario())
                .message(agendamentoRequest.getMessage())
                .tipoComunicacao(agendamentoRequest.getTipoComunicacao())
                .dataHoraEnvio(agendamentoRequest.getDataHoraEnvio())
                .createAt(agendamentoRequest.getDataHoraEnvio())
                .statusAgendamento(StatusAgendamento.AGENDADO)
                .createAt(LocalDateTime.now())
                .build();

        return toResponse(agendamentoRepository.save(agendamento));
                
    }

    public AgendamentoResponseDto buscarPorId(Long id){

        return toResponse(agendamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agendamento nao encontrado")));

    }

    public void deletarAgendamento(Long id) {
        try {
            
            if (!agendamentoRepository.existsById(id)) {
                throw new RuntimeException("Agendamento nao encontrado");
            }
                
            agendamentoRepository.deleteById(id);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public AgendamentoResponseDto toResponse(Agendamento agendamento) {

        return new AgendamentoResponseDto(
                agendamento.getId(),
                agendamento.getDestinatario(),
                agendamento.getMessage(),
                agendamento.getTipoComunicacao(),
                agendamento.getDataHoraEnvio(),
                agendamento.getStatusAgendamento(),
                agendamento.getCreateAt()
        );
    }

}
