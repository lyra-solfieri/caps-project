package com.lyra.caps.model;

import java.time.LocalDateTime;

import com.lyra.caps.dto.AnamneseDto;

import jakarta.persistence.Column;
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

@Data
@NoArgsConstructor
@Entity
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

    private Boolean sorriu;
    private Boolean equilibrouPescoco;
    private Boolean engatinhou;
    private Boolean sentou;
    private Boolean andou;
    private String primeirasPalavras;
    private Boolean falaCorreta;
    private Boolean trocouLetras;
    private Boolean gaguejou;
    private Boolean denticao;
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

    public static Anamnese fromDto( AnamneseDto anamneseDto) {

        if (anamneseDto == null) {
            return null;
        }

        Anamnese anamnese = new Anamnese();
        anamnese.setId(anamneseDto.getId());
        // anamnese.setPaciente(Paciente.fromDto(anamneseDto.getPacienteId()));
        anamnese.setProfissionalResponsavel(anamneseDto.getProfissionalResponsavel());
        anamnese.setQueixaPrincipal(anamneseDto.getQueixaPrincipal());
        anamnese.setHistoriaDoencaAtual(anamneseDto.getHistoriaDoencaAtual());
        anamnese.setEncaminhamentos(anamneseDto.getEncaminhamentos());
        anamnese.setAntecedentesFamiliares(anamneseDto.getAntecedentesFamiliares());
        anamnese.setHabitosDeVida(anamneseDto.getHabitosDeVida());
        anamnese.setObservacoesGerais(anamneseDto.getObservacoesGerais());
        anamnese.setAlergias(anamneseDto.getAlergias());
        anamnese.setCirurgias(anamneseDto.getCirurgias());
        anamnese.setMedicamentosUsados(anamneseDto.getMedicamentosUsados());
        anamnese.setHistoricoDeInternacao(anamneseDto.getHistoricoDeInternacao());
        anamnese.setObservacoesGestacao(anamneseDto.getObservacoesGestacao());
        anamnese.setLocalNascimento(anamneseDto.getLocalNascimento());
        anamnese.setTipoParto(anamneseDto.getTipoParto());
        anamnese.setPosicaoNascimento(anamneseDto.getPosicaoNascimento());
        anamnese.setChorouAoNascer(anamneseDto.getChorouAoNascer());
        anamnese.setNasceuVermelho(anamneseDto.getNasceuVermelho());
        anamnese.setNasceuRoxo(anamneseDto.getNasceuRoxo());
        anamnese.setAnoxia(anamneseDto.getAnoxia());
        anamnese.setIctericia(anamneseDto.getIctericia());
        anamnese.setPrecisouOxigenio(anamneseDto.getPrecisouOxigenio());
        anamnese.setIncubadora(anamneseDto.getIncubadora());
        anamnese.setAltaHospitalar(anamneseDto.getAltaHospitalar());
        anamnese.setSorriu(anamneseDto.getSorriu());
        anamnese.setEngatinhou(anamneseDto.getEngatinhou());
        anamnese.setSentou(anamneseDto.getSentou());
        anamnese.setAndou(anamneseDto.getAndou());
        anamnese.setEquilibrouPescoco(anamneseDto.getEquilibrouPescoco());
        anamnese.setPrimeirasPalavras(anamneseDto.getPrimeirasPalavras());
        anamnese.setFalaCorreta(anamneseDto.getFalaCorreta());
        anamnese.setTrocouLetras(anamneseDto.getTrocouLetras());
        anamnese.setGaguejou(anamneseDto.getGaguejou());
        anamnese.setDenticao(anamneseDto.getDenticao());
        anamnese.setControleEsfincterAnalDiurno(anamneseDto.getControleEsfincterAnalDiurno());
        anamnese.setControleEsfincterVesicalDiurno(anamneseDto.getControleEsfincterVesicalDiurno());
        anamnese.setControleEsfincterNoturno(anamneseDto.getControleEsfincterNoturno());
        anamnese.setCuidadosPorQuem(anamneseDto.getCuidadosPorQuem());
        anamnese.setUsouChupeta(anamneseDto.getUsouChupeta());
        anamnese.setChupouDedo(anamneseDto.getChupouDedo());
        anamnese.setRoeUnhas(anamneseDto.getRoeUnhas());
        anamnese.setPuxaOrelha(anamneseDto.getPuxaOrelha());
        anamnese.setArrancaCabelos(anamneseDto.getArrancaCabelos());
        anamnese.setMordeLabios(anamneseDto.getMordeLabios());
        anamnese.setTiques(anamneseDto.getTiques());
        anamnese.setAtitudeDianteHabitos(anamneseDto.getAtitudeDianteHabitos());
        anamnese.setDormeBem(anamneseDto.getDormeBem());
        anamnese.setPulaQuandoDorme(anamneseDto.getPulaQuandoDorme());
        anamnese.setBabaNoite(anamneseDto.getBabaNoite());
        anamnese.setSudorese(anamneseDto.getSudorese());
        anamnese.setAcordaVariasVezes(anamneseDto.getAcordaVariasVezes());
        anamnese.setFalaDormindo(anamneseDto.getFalaDormindo());
        anamnese.setGritaDormindo(anamneseDto.getGritaDormindo());
        anamnese.setRangeDentes(anamneseDto.getRangeDentes());
        anamnese.setSonambulo(anamneseDto.getSonambulo());
        anamnese.setPesadelos(anamneseDto.getPesadelos());
        anamnese.setAmamentadoNoPeito(anamneseDto.getAmamentadoNoPeito());
        anamnese.setAtitudeDesmame(anamneseDto.getAtitudeDesmame());
        anamnese.setHabitosAlimentares(anamneseDto.getHabitosAlimentares());
        anamnese.setComportamentoEscola(anamneseDto.getComportamentoEscola());
        anamnese.setGostaEstudar(anamneseDto.getGostaEstudar());
        anamnese.setHistoricoEscolar(anamneseDto.getHistoricoEscolar());
        anamnese.setGostaEscola(anamneseDto.getGostaEscola());
        anamnese.setQueixasComportamento(anamneseDto.getQueixasComportamento());
        anamnese.setDificuldadeAprendizagem(anamneseDto.getDificuldadeAprendizagem());
        anamnese.setPrefereBrincar(anamneseDto.getPrefereBrincar());
        anamnese.setAtivoOuPassivo(anamneseDto.getAtivoOuPassivo());
        anamnese.setObservacoesComportamentoSocial(anamneseDto.getObservacoesComportamentoSocial());
        anamnese.setObservacoesEstrategicas(anamneseDto.getObservacoesEstrategicas());
        anamnese.setProjetoTerapeuticoSingular(anamneseDto.getProjetoTerapeuticoSingular());
        return anamnese;
        
    }
}
