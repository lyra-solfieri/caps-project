package com.lyra.caps.dto;

import java.util.List;

import com.lyra.caps.model.enums.OcrStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RaasOcrUploadResponse {
    private String correlationId;
    private Long processamentoId;
    private OcrStatus status;
    private String mensagem;
    private List<String> erros;
}