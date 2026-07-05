package com.lyra.caps.dto;

import com.lyra.caps.model.Evolucao;
import com.lyra.caps.model.Paciente;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EvolucaoDto {

    private Long id;

    @NotNull(message = "Paciente é obrigatório")
    private Long pacienteId;

    private String pacienteNome;

    private Long codProntuario;

    private String dataHoraEdicao;

    private String nomeProfissional;

    @NotBlank(message = "Texto da evolução é obrigatório")
    @Size(min = 10, max = 1000, message = "Texto deve ter entre 10 e 1000 caracteres")
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

    public static Evolucao toEntity(EvolucaoDto dto, Paciente paciente) {
        Evolucao evolucao = new Evolucao();

        evolucao.setId(dto.getId());
        evolucao.setPaciente(paciente);
        evolucao.setCodProntuario(dto.getCodProntuario());
        evolucao.setDataHoraEdicao(dto.getDataHoraEdicao());
        evolucao.setNomeProfissional(dto.getNomeProfissional());
        evolucao.setTexto(dto.getTexto());

        return evolucao;
    }

}
