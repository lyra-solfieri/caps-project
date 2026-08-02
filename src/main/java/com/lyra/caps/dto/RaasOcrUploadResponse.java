package com.lyra.caps.dto;

import com.lyra.caps.model.enums.OcrStatus;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RaasOcrUploadResponse {
    private String correlationId;
    private Long processamentoId;
    private OcrStatus status;
    private String mensagem;
}
