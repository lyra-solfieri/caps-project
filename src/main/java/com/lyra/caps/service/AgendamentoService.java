package com.lyra.caps.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.lyra.caps.dto.AgendamentoRequestDto;
import com.lyra.caps.dto.AgendamentoResponseDto;
import com.lyra.caps.dto.PacienteDto;
import com.lyra.caps.dto.UserDto;
import com.lyra.caps.model.Agendamento;
import com.lyra.caps.model.Paciente;
import com.lyra.caps.model.User;
import com.lyra.caps.model.enums.StatusAgendamento;
import com.lyra.caps.repository.AgendamentoRepository;
import com.lyra.caps.repository.PacienteRepository;
import com.lyra.caps.repository.UserRepository;
import com.lyra.caps.util.LogPersonalizado;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgendamentoService implements IAgendamentoService {

    private final AgendamentoRepository agendamentoRepository;
    private final UserRepository userRepository;
    private final PacienteRepository pacienteService;

    public AgendamentoResponseDto agendar(AgendamentoRequestDto agendamentoRequest) {

        try {
            
            User usuarioLogado = userRepository.findById(agendamentoRequest.getIdUser())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            Paciente paciente = pacienteService.findById(agendamentoRequest.getIdPaciente())
                    .orElseThrow(() -> new RuntimeException("Paciente nao encontrado"));


            Agendamento agendamento = Agendamento.builder()
                    .destinatario(agendamentoRequest.getDestinatario())
                    .message(agendamentoRequest.getMessage())
                    .tipoComunicacao(agendamentoRequest.getTipoComunicacao())
                    .dataHoraEnvio(agendamentoRequest.getDataHoraEnvio())                    
                    .statusAgendamento(StatusAgendamento.AGENDADO)
                    .user(usuarioLogado)
                    .paciente(paciente)
                    .createAt(LocalDateTime.now())
                    .build();
    
            return toResponse(agendamentoRepository.save(agendamento));
                    
            
        } catch (Exception e) {
            LogPersonalizado.getLogger(this.getClass()).error("Erro ao agendar", e);
            e.printStackTrace();
            throw new RuntimeException("Erro ao agendar");
        }
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
            LogPersonalizado.getLogger(this.getClass()).error("Erro ao deletar agendamento", e);
            e.printStackTrace();
        }
    }

    public AgendamentoResponseDto toResponse(Agendamento agendamento) {

        UserDto userDto = null;
        if (agendamento.getUser() != null) {
            userDto = new UserDto(
                agendamento.getUser().getId(),
                agendamento.getUser().getNome(),
                agendamento.getUser().getEmail(),
                agendamento.getUser().getRole()
            );
        }

        PacienteDto pacienteDto = null;
        if (agendamento.getPaciente() != null) {
            pacienteDto = new PacienteDto();
        }

        return new AgendamentoResponseDto(
                agendamento.getId(),
                agendamento.getDestinatario(),
                agendamento.getMessage(),
                agendamento.getTipoComunicacao(),
                agendamento.getDataHoraEnvio(),
                agendamento.getStatusAgendamento(),
                agendamento.getCreateAt(),
                pacienteDto,
                userDto
        );
    }

    public String alertarAgendamento(Long id) {
        try {
            
            if (!agendamentoRepository.existsById(id)) {
                throw new RuntimeException("Agendamento nao encontrado");
            }
                
            return "Agendamento atual";
            
        } catch (Exception e) {
            e.printStackTrace();
            return "Sem agendamento";
        }
    }

    @Override
    public List<AgendamentoResponseDto> listarAgendamentos() {
        try {

            return agendamentoRepository.findAll().stream().map(this::toResponse).toList();
            
        } catch (Exception e) {            
            e.printStackTrace();
            return null;
        }
    }

}
