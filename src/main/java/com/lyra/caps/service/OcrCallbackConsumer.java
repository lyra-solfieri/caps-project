package com.lyra.caps.service;

import java.time.LocalDateTime;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lyra.caps.dto.OcrResultMessage;
import com.lyra.caps.model.enums.OcrStatus;
import com.lyra.caps.repository.OcrProcessamentoRepository;
import com.lyra.caps.util.LogPersonalizado;

@Component
public class OcrCallbackConsumer {

    private final OcrProcessamentoRepository repository;
    private final ObjectMapper objectMapper;

    public OcrCallbackConsumer(OcrProcessamentoRepository repository,
                               ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "${ocr.queue.response}")
    public void onOcrResult(String message) {
        var log = LogPersonalizado.getLogger(this.getClass());
        try {
            OcrResultMessage result = objectMapper.readValue(message, OcrResultMessage.class);
            log.info(String.format("Resultado OCR recebido: correlationId=%s, status=%s",
                    result.getCorrelationId(), result.getStatus()));

            var processamento = repository.findByCorrelationId(result.getCorrelationId())
                    .orElse(null);
            if (processamento == null) {
                log.error(String.format("Processamento não encontrado para correlationId=%s",
                        result.getCorrelationId()));
                return;
            }

            processamento.setStatus(OcrStatus.valueOf(result.getStatus()));
            processamento.setConfidenceGeral(result.getConfidenceGeral());
            processamento.setDataConclusao(LocalDateTime.now());

            if (result.getDadosExtraidos() != null) {
                processamento.setDadosExtraidosJson(
                    objectMapper.writeValueAsString(result.getDadosExtraidos()));
            }
            processamento.setResultadoJson(message);

            repository.save(processamento);

            log.info(String.format("Processamento atualizado: correlationId=%s, status=%s",
                    result.getCorrelationId(), result.getStatus()));

        } catch (Exception e) {
            log.error("Erro ao processar resultado OCR", e);
        }
    }
}
