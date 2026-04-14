package com.lyra.caps.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.lyra.caps.dto.EvolucaoDto;

public interface IEvolucaoService {

    EvolucaoDto save(EvolucaoDto evolucaoDto);

    EvolucaoDto findById(Long id);

    void delete(Long id);

    EvolucaoDto update(Long id, EvolucaoDto evolucaoDto);

    Page<EvolucaoDto> listarAnamneses(Pageable pageable);

}
