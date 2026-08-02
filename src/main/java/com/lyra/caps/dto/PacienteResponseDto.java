package com.lyra.caps.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lyra.caps.model.Paciente;
import com.lyra.caps.model.enums.Genero;
import com.lyra.caps.model.enums.Raca;

import lombok.Data;


@Data
public class PacienteResponseDto {

    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private String cpf;
    private EnderecoDto endereco;
    private Genero genero;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataNascimento;
    private String nomeMae;
    private String nomePai;
    private String profissao;
    private String numeroCartaoSUS;
    private Raca raca;
    private String numeroProntuario;
    private boolean infantojuvenil;
    private boolean emSituacaoSocioeconomica;

  public static PacienteResponseDto fromEntity(Paciente paciente) {
        if (paciente == null) {
            return null;
        }

        PacienteResponseDto dto = new PacienteResponseDto();
        dto.setId(paciente.getId());
        dto.setNome(paciente.getNome());
        dto.setEmail(paciente.getEmail());
        dto.setTelefone(paciente.getTelefone());
        dto.setCpf(paciente.getCpf());
        dto.setGenero(paciente.getGenero());
        dto.setDataNascimento(paciente.getDataNascimento());
        dto.setNomeMae(paciente.getNomeMae());
        dto.setNomePai(paciente.getNomePai());
        dto.setProfissao(paciente.getProfissao());
        dto.setNumeroCartaoSUS(paciente.getNumeroCartaoSUS());
        dto.setRaca(paciente.getRaca());
        dto.setNumeroProntuario(paciente.getNumeroProntuario());
        dto.setInfantojuvenil(paciente.isInfantojuvenil());
        dto.setEmSituacaoSocioeconomica(paciente.isEmSituacaoSocioeconomica());

        if (paciente.getEndereco() != null) {
            dto.setEndereco(EnderecoDto.fromEntity(paciente.getEndereco()));
        }

        return dto;
    }
}
