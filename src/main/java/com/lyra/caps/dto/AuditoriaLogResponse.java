package com.lyra.caps.dto;

import java.time.LocalDateTime;

import com.lyra.caps.model.AuditoriaLog;
import com.lyra.caps.model.enums.AcaoAuditoria;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuditoriaLogResponse {
    private Long id;
    private String usuario;
    private AcaoAuditoria acao;
    private String entidade;
    private Long entidadeId;
    private String detalhes;
    private String ip;
    private LocalDateTime dataHora;

    public static AuditoriaLogResponse fromEntity(AuditoriaLog log) {
        if (log == null) {
            return null;
        }
        return new AuditoriaLogResponse(
                log.getId(),
                log.getUsuario(),
                log.getAcao(),
                log.getEntidade(),
                log.getEntidadeId(),
                log.getDetalhes(),
                log.getIp(),
                log.getDataHora()
        );
    }
}
