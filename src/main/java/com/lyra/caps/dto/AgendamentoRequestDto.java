package com.lyra.caps.dto;

import java.time.LocalDateTime;

import com.lyra.caps.model.enums.TipoComunicacao;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class AgendamentoRequestDto {


    private String destinatario;

    @NotBlank(message = "O message não pode ser vazio")
    private String message;

    @NotBlank(message = "O tipoComunicacao não pode ser vazio")
    private TipoComunicacao tipoComunicacao;

    @NotBlank(message = "O dataHoraEnvio não pode ser vazio")
    @Future(message = "A dataHoraEnvio deve ser futura")
    private LocalDateTime dataHoraEnvio;

    @NotBlank(message = "O usuario não pode ser vazio")
    private Long idUser;

    @NotBlank(message = "O paciente não pode ser vazio")
    private Long idPaciente;

}
