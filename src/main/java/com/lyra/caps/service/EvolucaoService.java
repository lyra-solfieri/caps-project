package com.lyra.caps.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lyra.caps.dto.EvolucaoDto;
import com.lyra.caps.model.Paciente;
import com.lyra.caps.repository.EvolucaoRepository;
import com.lyra.caps.repository.PacienteRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class EvolucaoService implements IEvolucaoService {

    private final EvolucaoRepository evolucaoRepository;
    private final PacienteRepository pacienteRepository;

    @Override
    @Transactional
    public EvolucaoDto save(EvolucaoDto evolucaoDto) {
        Paciente paciente = pacienteRepository.findById(evolucaoDto.getPacienteId())
                .orElseThrow(() -> new EntityNotFoundException("Paciente não encontrado: " + evolucaoDto.getPacienteId()));

        var evolucao = EvolucaoDto.toEntity(evolucaoDto, paciente);
        evolucao = evolucaoRepository.save(evolucao);
        log.info("Evolução criada: id={}, pacienteId={}", evolucao.getId(), evolucaoDto.getPacienteId());
        return EvolucaoDto.fromEntity(evolucao);
    }

    @Override
    public EvolucaoDto findById(Long id) {
        var evolucao = evolucaoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Evolução não encontrada: " + id));
        return EvolucaoDto.fromEntity(evolucao);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!evolucaoRepository.existsById(id)) {
            throw new EntityNotFoundException("Evolução não encontrada: " + id);
        }
        evolucaoRepository.deleteById(id);
        log.info("Evolução excluída: id={}", id);
    }

    @Override
    @Transactional
    public EvolucaoDto update(Long id, EvolucaoDto evolucaoDto) {
        var evolucao = evolucaoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Evolução não encontrada: " + id));

        if (evolucaoDto.getTexto() != null) {
            evolucao.setTexto(evolucaoDto.getTexto());
        }
        if (evolucaoDto.getNomeProfissional() != null) {
            evolucao.setNomeProfissional(evolucaoDto.getNomeProfissional());
        }
        if (evolucaoDto.getDataHoraEdicao() != null) {
            evolucao.setDataHoraEdicao(evolucaoDto.getDataHoraEdicao());
        }
        if (evolucaoDto.getCodProntuario() != null) {
            evolucao.setCodProntuario(evolucaoDto.getCodProntuario());
        }

        evolucao = evolucaoRepository.save(evolucao);
        log.info("Evolução atualizada: id={}", id);
        return EvolucaoDto.fromEntity(evolucao);
    }

    @Override
    public Page<EvolucaoDto> listar(Pageable pageable) {
        return evolucaoRepository.findAll(pageable)
                .map(EvolucaoDto::fromEntity);
    }

    @Override
    public List<EvolucaoDto> listarPorPaciente(Long pacienteId) {
        return evolucaoRepository.findByPacienteId(pacienteId).stream()
                .map(EvolucaoDto::fromEntity)
                .toList();
    }

}
