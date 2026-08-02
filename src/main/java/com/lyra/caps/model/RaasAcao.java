package com.lyra.caps.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
@Data
@Entity
public class RaasAcao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String codigoAcao;
    private Integer quantidade;
    private LocalDate data;
    private String cid10;
    private String servico;
    private String classificacao;
    private String cboExecutor;
    private String cartaoProfissional;
    private String codigoEquipe;

    private boolean caps;
    private boolean territorio;

    @ManyToOne
    @JoinColumn(name = "raas_id")
    private Raas raas;
}