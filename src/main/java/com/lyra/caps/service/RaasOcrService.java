package com.lyra.caps.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lyra.caps.dto.OcrResultMessage;
import com.lyra.caps.dto.RaasOcrUploadResponse;
import com.lyra.caps.model.OcrProcessamento;
import com.lyra.caps.model.enums.OcrStatus;
import com.lyra.caps.repository.OcrProcessamentoRepository;
import com.lyra.caps.util.LogPersonalizado;

@Service
public class RaasOcrService {

    private final OcrGatewayService ocrGateway;
    private final OcrProcessamentoRepository repository;
    private final ObjectMapper objectMapper;

    public RaasOcrService(OcrGatewayService ocrGateway,
                          OcrProcessamentoRepository repository,
                          ObjectMapper objectMapper) {
        this.ocrGateway = ocrGateway;
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    public RaasOcrUploadResponse upload(MultipartFile file, Long pacienteId) {
        
        String correlationId = ocrGateway.enviarParaOcr(file, pacienteId);

        OcrProcessamento proc = new OcrProcessamento();
        proc.setCorrelationId(correlationId);
        proc.setOriginalFileName(file.getOriginalFilename());
        proc.setPacienteId(pacienteId);
        proc.setStatus(OcrStatus.EM_PROCESSAMENTO);
        proc.setDataCriacao(LocalDateTime.now());
        repository.save(proc);

        LogPersonalizado.getLogger(this.getClass())
            .info(String.format("RAAS enviado para OCR: correlationId=%s, file=%s",
                    correlationId, file.getOriginalFilename()));

        return new RaasOcrUploadResponse(
            correlationId, proc.getId(), OcrStatus.EM_PROCESSAMENTO,
            "Documento enviado para processamento OCR");
    }

    public Optional<OcrProcessamento> consultarStatus(String correlationId) {
        return repository.findByCorrelationId(correlationId);
    }

    public Optional<OcrProcessamento> consultarStatusPorId(Long id) {
        return repository.findById(id);
    }

    public Optional<OcrResultMessage> getResultado(String correlationId) {
        return repository.findByCorrelationId(correlationId)
            .filter(p -> p.getResultadoJson() != null)
            .map(p -> {
                try {
                    return objectMapper.readValue(p.getResultadoJson(), OcrResultMessage.class);
                } catch (JsonProcessingException e) {
                    return null;
                }
            });
    }
}
