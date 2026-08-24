package com.lyra.caps.dto;

import java.time.LocalDate;
import java.util.List;

import com.lyra.caps.model.Raas;
import com.lyra.caps.model.RaasAcao;

import lombok.Data;

@Data
public class RaasResponseDTO {

    private Long id;

    private String nomeEstabelecimento;
    private String cnes;

    private String nomePaciente;
    private String cartaoSus;
    private String sexo;
    private String dataNascimento;
    private String nomeMae;

    private LocalDate dataAdmissao;
    private LocalDate dataConclusao;
    private String mesAtendimento;
    private Long numeroAutorizacao;

    private boolean status;
    private boolean enviadoSUS;
    private LocalDate dataGeracao;

    private Long pacienteId;
    private Long atendimentoId;

    private List<RaasAcaoDto> acoes;

    public static RaasResponseDTO fromEntity(Raas raas) {
        RaasResponseDTO dto = new RaasResponseDTO();
        dto.setId(raas.getId());
        dto.setNomeEstabelecimento(raas.getNomeEstabelecimento());
        dto.setCnes(raas.getCnes());

        dto.setNomePaciente(raas.getNomePaciente());
        dto.setCartaoSus(raas.getCartaoSus());
        dto.setSexo(raas.getSexo());
        dto.setDataNascimento(raas.getDataNascimento());
        dto.setNomeMae(raas.getNomeMae());

        dto.setDataAdmissao(raas.getDataAdmissao());
        dto.setDataConclusao(raas.getDataConclusao());
        dto.setMesAtendimento(raas.getMesAtendimento());
        dto.setNumeroAutorizacao(raas.getNumeroAutorizacao());

        dto.setStatus(raas.isStatus());
        dto.setEnviadoSUS(raas.isEnviadoSUS());
        dto.setDataGeracao(raas.getDataGeracao());

        if (raas.getPaciente() != null) {
            dto.setPacienteId(raas.getPaciente().getId());
        }
        if (raas.getAtendimento() != null) {
            dto.setAtendimentoId(raas.getAtendimento().getId());
        }

        if (raas.getAcoes() != null) {
            dto.setAcoes(raas.getAcoes().stream().map(RaasResponseDTO::toAcaoDto).toList());
        } else {
            dto.setAcoes(List.of());
        }

        return dto;
    }

    private static RaasAcaoDto toAcaoDto(RaasAcao acao) {
        RaasAcaoDto dto = new RaasAcaoDto();
        dto.setCodigoAcao(acao.getCodigoAcao());
        dto.setQuantidade(acao.getQuantidade());
        dto.setData(acao.getData());
        dto.setCid10(acao.getCid10());
        dto.setServico(acao.getServico());
        dto.setClassificacao(acao.getClassificacao());
        dto.setCboExecutor(acao.getCboExecutor());
        dto.setCartaoProfissional(acao.getCartaoProfissional());
        dto.setCodigoEquipe(acao.getCodigoEquipe());
        dto.setCaps(acao.isCaps());
        dto.setTerritorio(acao.isTerritorio());
        return dto;
    }
}
