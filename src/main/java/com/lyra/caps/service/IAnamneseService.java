package com.lyra.caps.service;

import java.util.List;

import com.lyra.caps.dto.AnamneseDto;

public interface IAnamneseService {

    AnamneseDto save(AnamneseDto anamneseDto);

    AnamneseDto findByPacienteId(Long id);

    AnamneseDto findById(Long id);

    void delete(Long id);

    AnamneseDto update(Long id, AnamneseDto anamneseDto);

    List<AnamneseDto> listarAnamneses();

    Long listarQuantidade();

}
