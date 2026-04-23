package com.lyra.caps.service;

import java.util.List;

import com.lyra.caps.dto.RaasRequestDTO;
import com.lyra.caps.model.Raas;

public interface IRaasService {


    Raas gerarRaas(RaasRequestDTO dto);


    Raas buscarPorId(Long id);


    List<Raas> listarPorPaciente(Long pacienteId);


    void deletar(Long id);


    byte[] gerarPdf(Long raasId);
}