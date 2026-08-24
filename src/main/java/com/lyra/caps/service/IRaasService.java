package com.lyra.caps.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.lyra.caps.dto.RaasPageResponse;
import com.lyra.caps.dto.RaasRequestDTO;
import com.lyra.caps.dto.RaasResponseDTO;

public interface IRaasService {


    RaasResponseDTO gerarRaas(RaasRequestDTO dto);


    RaasResponseDTO atualizar(Long id, RaasRequestDTO dto);


    RaasResponseDTO buscarPorId(Long id);


    List<RaasResponseDTO> listarPorPaciente(Long pacienteId);


    RaasPageResponse listarPaginado(Pageable pageable);


    void deletar(Long id);


    byte[] gerarPdf(Long raasId);
}
