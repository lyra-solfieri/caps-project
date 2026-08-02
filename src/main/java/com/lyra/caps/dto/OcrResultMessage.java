package com.lyra.caps.dto;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class OcrResultMessage {
    private String correlationId;
    private String status;
    private Double confidenceGeral;
    private Map<String, Object> dadosExtraidos;
    private Map<String, Double> confidences;
    private List<String> camposNaoReconhecidos;
    private List<String> camposBaixaConfianca;
    private List<String> erros;
}
