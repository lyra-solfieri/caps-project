package com.lyra.caps.dto;

import com.lyra.caps.model.Evolucao;

import lombok.Data;

@Data
public class EvolucaoDto {

    private Long id;
    private Long pacienteId;
    private String pacienteNome;

    private Long codProntuario;

    private String dataHoraEdicao;

    private String nomeProfissional;

    private String texto;

    public static EvolucaoDto fromEntity(Evolucao evolucao) {

        EvolucaoDto dto = new EvolucaoDto();

        dto.setId(evolucao.getId());

        if (evolucao.getPaciente() != null) {
            dto.setPacienteId(evolucao.getPaciente().getId());
            dto.setPacienteNome(evolucao.getPaciente().getNome());
        }

        dto.setCodProntuario(evolucao.getCodProntuario());
        dto.setDataHoraEdicao(evolucao.getDataHoraEdicao());
        dto.setNomeProfissional(evolucao.getNomeProfissional());
        dto.setTexto(evolucao.getTexto());

        return dto;
    }

    public static Evolucao toEntity(EvolucaoDto dto) {

        Evolucao evolucao = new Evolucao();

        evolucao.setId(dto.getId());
        evolucao.setCodProntuario(dto.getCodProntuario());
        evolucao.setDataHoraEdicao(dto.getDataHoraEdicao());
        evolucao.setNomeProfissional(dto.getNomeProfissional());
        evolucao.setTexto(dto.getTexto());

        return evolucao;
    }

}
