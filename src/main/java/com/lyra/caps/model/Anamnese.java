package com.lyra.caps.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "anamnese")
public class Anamnese {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    private LocalDateTime dataRegistro;

    private String profissionalResponsavel;

    @Column(columnDefinition = "TEXT")
    private String queixaPrincipal;

    @Column(columnDefinition = "TEXT")
    private String historiaDoencaAtual;

    @Column(columnDefinition = "TEXT")
    private String encaminhamentos;

    @Column(columnDefinition = "TEXT")
    private String antecedentesFamiliares;

    @Column(columnDefinition = "TEXT")
    private String habitosDeVida;

    @Column(columnDefinition = "TEXT")
    private String observacoesGerais;

    private String alergias;
    private String cirurgias;

    @Column(columnDefinition = "TEXT")
    private String medicamentosUsados;

    @Column(columnDefinition = "TEXT")
    private String historicoDeInternacao;

    @Column(columnDefinition = "TEXT")
    private String observacoesGestacao;

    private String localNascimento;
    private String tipoParto;
    private String posicaoNascimento;


    private Boolean chorouAoNascer;
    private Boolean nasceuVermelho;
    private Boolean nasceuRoxo;
    private Boolean anoxia;
    private Boolean ictericia;
    private Boolean precisouOxigenio;
    private Boolean incubadora;

    private String altaHospitalar;

    private String sorriu;
    private String equilibrouPescoco;
    private String engatinhou;
    private String sentou;
    private String andou;
    private String primeirasPalavras;
    private String falaCorreta;
    private String trocouLetras;
    private String gaguejou;
    private String denticao;
    private String controleEsfincterAnalDiurno;
    private String controleEsfincterVesicalDiurno;
    private String controleEsfincterNoturno;
    private String cuidadosPorQuem;


    private Boolean usouChupeta;
    private Boolean chupouDedo;
    private Boolean roeUnhas;
    private Boolean puxaOrelha;
    private Boolean arrancaCabelos;
    private Boolean mordeLabios;
    private Boolean tiques;

    @Column(columnDefinition = "TEXT")
    private String atitudeDianteHabitos;

    private Boolean dormeBem;
    private Boolean pulaQuandoDorme;
    private Boolean babaNoite;
    private Boolean sudorese;
    private Boolean acordaVariasVezes;
    private Boolean falaDormindo;
    private Boolean gritaDormindo;
    private Boolean rangeDentes;
    private Boolean sonambulo;
    private Boolean pesadelos;


    private Boolean amamentadoNoPeito;

    @Column(columnDefinition = "TEXT")
    private String atitudeDesmame;

    @Column(columnDefinition = "TEXT")
    private String habitosAlimentares;


    @Column(columnDefinition = "TEXT")
    private String comportamentoEscola;

    private Boolean gostaEstudar;

    @Column(columnDefinition = "TEXT")
    private String historicoEscolar;

    private Boolean gostaEscola;

    @Column(columnDefinition = "TEXT")
    private String queixasComportamento;

    private Boolean dificuldadeAprendizagem;


    private String prefereBrincar;
    private String ativoOuPassivo;

    @Column(columnDefinition = "TEXT")
    private String observacoesComportamentoSocial;


    @Column(columnDefinition = "TEXT")
    private String observacoesEstrategicas;

    @Column(columnDefinition = "TEXT")
    private String projetoTerapeuticoSingular;
}
