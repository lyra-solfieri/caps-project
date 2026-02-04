package com.lyra.caps.dto;

import java.time.LocalDateTime;

import com.lyra.caps.model.enums.TipoComunicacao;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class AgendamentoRequestDto {


    private String destinatario;

    @NotBlank(message = "O message não pode ser vazio")
    private String message;

    @NotNull(message = "O tipoComunicacao não pode ser vazio")
    private TipoComunicacao tipoComunicacao;

    @NotNull(message = "O dataHoraAtendimento não pode ser vazio")    
    private LocalDateTime dataHoraAtendimento;

    
    private Long idUser;

    private Long idPaciente;

}
