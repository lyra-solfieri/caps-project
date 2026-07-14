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
        log.debug("Criando evolução para pacienteId={}", evolucaoDto.getPacienteId());
        try {
            Paciente paciente = pacienteRepository.findById(evolucaoDto.getPacienteId())
                    .orElseThrow(() -> new EntityNotFoundException("Paciente não encontrado: " + evolucaoDto.getPacienteId()));

            var evolucao = EvolucaoDto.toEntity(evolucaoDto, paciente);
            evolucao = evolucaoRepository.save(evolucao);
            log.info("Evolução criada: id={}, pacienteId={}", evolucao.getId(), evolucaoDto.getPacienteId());
            return EvolucaoDto.fromEntity(evolucao);
        } catch (Exception e) {
            log.error("Erro ao criar evolução para pacienteId={}", evolucaoDto.getPacienteId(), e);
            throw e;
        }
    }

    @Override
    public EvolucaoDto findById(Long id) {
        log.debug("Buscando evolução por id={}", id);
        try {
            var evolucao = evolucaoRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Evolução não encontrada: " + id));
            log.info("Evolução encontrada: id={}", id);
            return EvolucaoDto.fromEntity(evolucao);
        } catch (Exception e) {
            log.error("Erro ao buscar evolução id={}", id, e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.debug("Excluindo evolução id={}", id);
        try {
            if (!evolucaoRepository.existsById(id)) {
                throw new EntityNotFoundException("Evolução não encontrada: " + id);
            }
            evolucaoRepository.deleteById(id);
            log.info("Evolução excluída: id={}", id);
        } catch (Exception e) {
            log.error("Erro ao excluir evolução id={}", id, e);
            throw e;
        }
    }

    @Override
    @Transactional
    public EvolucaoDto update(Long id, EvolucaoDto evolucaoDto) {
        log.debug("Atualizando evolução id={}", id);
        try {
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
        } catch (Exception e) {
            log.error("Erro ao atualizar evolução id={}", id, e);
            throw e;
        }
    }

    @Override
    public Page<EvolucaoDto> listar(Pageable pageable) {
        log.debug("Listando evoluções com pageable={}", pageable);
        try {
            Page<EvolucaoDto> result = evolucaoRepository.findAll(pageable)
                    .map(EvolucaoDto::fromEntity);
            log.info("Evoluções listadas: total={}", result.getTotalElements());
            return result;
        } catch (Exception e) {
            log.error("Erro ao listar evoluções", e);
            throw e;
        }
    }

    @Override
    public List<EvolucaoDto> listarPorPaciente(Long pacienteId) {
        log.debug("Listando evoluções por pacienteId={}", pacienteId);
        try {
            List<EvolucaoDto> result = evolucaoRepository.findByPacienteId(pacienteId).stream()
                    .map(EvolucaoDto::fromEntity)
                    .toList();
            log.info("Evoluções do paciente {} listadas: total={}", pacienteId, result.size());
            return result;
        } catch (Exception e) {
            log.error("Erro ao listar evoluções do pacienteId={}", pacienteId, e);
            throw e;
        }
    }

}
