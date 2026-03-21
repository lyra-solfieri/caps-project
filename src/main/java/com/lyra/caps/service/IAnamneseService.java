package com.lyra.caps.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import com.lyra.caps.dto.AnamneseDto;

public interface IAnamneseService {

    AnamneseDto save(AnamneseDto anamneseDto);

    AnamneseDto findByPacienteId(Long id);

    AnamneseDto findById(Long id);

    void delete(Long id);

    AnamneseDto update(Long id, AnamneseDto anamneseDto);

    Page<AnamneseDto> listarAnamneses(Pageable pageable);

    Long listarQuantidade();

}
