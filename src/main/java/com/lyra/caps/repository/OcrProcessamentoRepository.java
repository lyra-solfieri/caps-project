package com.lyra.caps.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lyra.caps.model.OcrProcessamento;

public interface OcrProcessamentoRepository extends JpaRepository<OcrProcessamento, Long> {
    Optional<OcrProcessamento> findByCorrelationId(String correlationId);
}
