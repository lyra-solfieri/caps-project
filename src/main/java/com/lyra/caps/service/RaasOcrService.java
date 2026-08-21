package com.lyra.caps.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

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

    private final OcrClientService ocrClient;
    private final OcrProcessamentoRepository repository;
    private final ObjectMapper objectMapper;

    public RaasOcrService(OcrClientService ocrClient,
                          OcrProcessamentoRepository repository,
                          ObjectMapper objectMapper) {
        this.ocrClient = ocrClient;
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    public RaasOcrUploadResponse upload(MultipartFile file, Long pacienteId) {

        String correlationId = UUID.randomUUID().toString();

        // Processamento síncrono: envia o arquivo ao microserviço de OCR via REST.
        OcrResultMessage resultado = ocrClient.processar(file, correlationId);

        OcrProcessamento proc = new OcrProcessamento();
        proc.setCorrelationId(correlationId);
        proc.setOriginalFileName(file.getOriginalFilename());
        proc.setPacienteId(pacienteId);
        proc.setStatus(statusOuFalha(resultado));
        proc.setConfidenceGeral(resultado.getConfidenceGeral());
        proc.setDataCriacao(LocalDateTime.now());
        proc.setDataConclusao(LocalDateTime.now());

        if (resultado.getDadosExtraidos() != null) {
            try {
                proc.setDadosExtraidosJson(
                    objectMapper.writeValueAsString(resultado.getDadosExtraidos()));
            } catch (JsonProcessingException e) {
                LogPersonalizado.getLogger(this.getClass())
                    .error("Erro ao serializar dados extraídos", e);
            }
        }
        try {
            proc.setResultadoJson(objectMapper.writeValueAsString(resultado));
        } catch (JsonProcessingException e) {
            LogPersonalizado.getLogger(this.getClass())
                .error("Erro ao serializar resultado OCR", e);
        }
        repository.save(proc);

        boolean sucesso = proc.getStatus() != OcrStatus.FALHA;
        String mensagem = sucesso
            ? "Documento processado com sucesso"
            : "Não foi possível processar o documento. Revise o arquivo e tente novamente.";

        LogPersonalizado.getLogger(this.getClass())
            .info(String.format("RAAS processado por OCR: correlationId=%s, file=%s, status=%s, mensagem=%s, erros=%s",
                    correlationId, file.getOriginalFilename(), proc.getStatus(), mensagem, resultado.getErros()));

        return new RaasOcrUploadResponse(
            correlationId, proc.getId(), proc.getStatus(),
            mensagem, resultado.getErros());
    }

    private OcrStatus statusOuFalha(OcrResultMessage resultado) {
        String status = resultado != null ? resultado.getStatus() : null;
        if (status == null) {
            return OcrStatus.FALHA;
        }
        try {
            return OcrStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            return OcrStatus.FALHA;
        }
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
