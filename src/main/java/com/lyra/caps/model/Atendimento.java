package com.lyra.caps.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "atendimento")
public class Atendimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate dataAdmissao;
    private LocalDate dataConclusao;
    private String mesAtendimento;

    private Long numeroAutorizacao;

    private String usoDrogas;

    private boolean alcool;
    private boolean crack;
    private boolean outrasDrogas;

    private boolean origemDemandaEspontanea;
    private boolean origemAtencaoBasica;
    private boolean origemUrgencia;
    private boolean origemCaps;
    private boolean origemHospitalGeral;
    private boolean origemHospitalPsiquiatrico;

    private String cidPrincipal;
    private String descricaoCidPrincipal;

    private String cidSecundario;
    private String descricaoCidSecundario;

    private String esf;

    private String cnesAtendimento;

    private boolean encaminhamentoCaps;
    private boolean encaminhamentoAtencaoBasica;
    private boolean alta;
    private boolean obito;


    private String observacao;

  
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;
}