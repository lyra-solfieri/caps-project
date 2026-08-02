package com.lyra.caps.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Data
@Entity
public class Raas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @ManyToOne
    private Paciente paciente;

    @ManyToOne
    private Atendimento atendimento;

    private LocalDate dataGeracao;

    @OneToMany(mappedBy = "raas", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RaasAcao> acoes = new ArrayList<>();
}

