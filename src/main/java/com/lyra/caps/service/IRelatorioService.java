package com.lyra.caps.service;

import com.lyra.caps.dto.relatorio.IndicadoresDesempenhoDto;
import com.lyra.caps.dto.relatorio.AtendimentoPorProfissionalDto;

import java.util.List;

public interface IRelatorioService {
    IndicadoresDesempenhoDto obterIndicadores(String inicio, String fim);
    List<AtendimentoPorProfissionalDto> obterAtendimentosPorProfissional(String inicio, String fim);
}
