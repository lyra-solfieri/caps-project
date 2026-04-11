package com.lyra.caps.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.lyra.caps.dto.EvolucaoDto;
import com.lyra.caps.repository.EvolucaoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class EvolucaoService implements IEvolucaoService {

    private final EvolucaoRepository evolucaoRepository;

    @Override
    public EvolucaoDto save(EvolucaoDto evolucaoDto) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'save'");
    }

    @Override
    public EvolucaoDto findById(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findById'");
    }

    @Override
    public void delete(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public EvolucaoDto update(Long id, EvolucaoDto evolucaoDto) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public Page<EvolucaoDto> listarAnamneses(Pageable pageable) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'listarAnamneses'");
    }

}
