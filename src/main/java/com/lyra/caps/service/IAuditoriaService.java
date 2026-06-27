package com.lyra.caps.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;

import com.lyra.caps.dto.AuditoriaLogResponse;
import com.lyra.caps.model.enums.AcaoAuditoria;

public interface IAuditoriaService {
    Page<AuditoriaLogResponse> listarLogs(int page, int size, AcaoAuditoria acao, String entidade, String usuario, LocalDateTime dataInicio, LocalDateTime dataFim);
    List<String> listarUsuarios();
    List<String> listarEntidades();
}
