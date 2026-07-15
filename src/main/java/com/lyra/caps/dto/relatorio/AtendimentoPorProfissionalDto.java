package com.lyra.caps.dto.relatorio;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AtendimentoPorProfissionalDto {
    private String mes;
    private long quantidade;
}
