package com.lyra.caps.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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
import com.lyra.caps.util.LogPersonalizado;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgendamentoService implements IAgendamentoService {

    private final AgendamentoRepository agendamentoRepository;
    private final PacienteRepository pacienteService;
    private final UserService userService;

    public AgendamentoResponseDto agendar(AgendamentoRequestDto agendamentoRequest) {

        try {

            User usuarioLogado = null;
            Paciente paciente = null;

            usuarioLogado = userService.getCurrentUser();
            
            Long idPaciente = agendamentoRequest.getPacienteId();

            if (idPaciente != null && idPaciente > 0) {

                paciente = pacienteService.findById(idPaciente)
                    .orElseThrow(() -> new RuntimeException("Paciente nao encontrado"));
            }

            
            Agendamento agendamento = Agendamento.builder()
                    .destinatario(agendamentoRequest.getDestinatario())
                    .message(agendamentoRequest.getMessage())
                    .tipoComunicacao(agendamentoRequest.getTipoComunicacao())
                    .dataHoraAtendimento(agendamentoRequest.getDataHoraAtendimento())                    
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

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public AgendamentoResponseDto buscarPorId(@PathVariable Long id) {
        Agendamento agendamento = agendamentoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Agendamento não encontrado"));
            
        return toResponse(agendamento); 
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
            
            pacienteDto.setId(agendamento.getPaciente().getId());
            pacienteDto.setNome(agendamento.getPaciente().getNome());
            pacienteDto.setEmail(agendamento.getPaciente().getEmail());
            pacienteDto.setCpf(agendamento.getPaciente().getCpf());
        }

        return new AgendamentoResponseDto(
                agendamento.getId(),
                agendamento.getDestinatario(),
                agendamento.getMessage(),
                agendamento.getTipoComunicacao(),
                agendamento.getDataHoraAtendimento(),
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

   @Override
    public AgendamentoResponseDto atualizarAgendamento(Long id, AgendamentoRequestDto dto) {
        // 1. Busca o agendamento antigo
        Agendamento agendamento = agendamentoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Agendamento não encontrado"));

        // 2. Busca o paciente atualizado (caso ele tenha trocado o paciente)
        Paciente paciente = pacienteService.findById(dto.getPacienteId())
            .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));

        // 3. Atualiza os dados
        agendamento.setPaciente(paciente);
        agendamento.setTipoComunicacao(dto.getTipoComunicacao());
        agendamento.setDestinatario(dto.getDestinatario());
        agendamento.setMessage(dto.getMessage());
        agendamento.setDataHoraAtendimento(dto.getDataHoraAtendimento());

        Agendamento agendamentoSalvo = agendamentoRepository.save(agendamento);

        return toResponse(agendamentoSalvo);
    }

}
