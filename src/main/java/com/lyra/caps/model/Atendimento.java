package com.lyra.caps.model;

import java.time.LocalDate;
import java.time.LocalTime;

import com.lyra.caps.model.enums.OrigemPaciente;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Getter
@Setter
@Data
@Table(name = "atendimento")
public class Atendimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate data;
    private LocalDate dataDeConclusao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrigemPaciente origemPaciente;

    private LocalTime hora;
    private String observacao;
    private String encaminhamento;
    private Long numeroDaAutorizacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

}
