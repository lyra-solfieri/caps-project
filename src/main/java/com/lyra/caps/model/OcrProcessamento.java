package com.lyra.caps.model;

import java.time.LocalDateTime;

import com.lyra.caps.model.enums.OcrStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.Data;

@Data
@Entity
public class OcrProcessamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String correlationId;
    private String originalFileName;
    private Long pacienteId;

    private Long pacienteCriadoId;
    private Long atendimentoCriadoId;
    private Long raasCriadoId;

    @Enumerated(EnumType.STRING)
    private OcrStatus status;

    private Double confidenceGeral;

    @Lob
    private String dadosExtraidosJson;

    @Lob
    private String resultadoJson;

    private LocalDateTime dataCriacao;
    private LocalDateTime dataConclusao;
}
