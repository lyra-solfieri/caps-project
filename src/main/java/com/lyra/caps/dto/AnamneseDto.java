package com.lyra.caps.dto;

import java.time.LocalDateTime;

import com.lyra.caps.model.Anamnese;

import lombok.Data;

@Data
public class AnamneseDto {

    private Long id;

    private Long pacienteId;

    private LocalDateTime dataRegistro;

    private String profissionalResponsavel;

    private String queixaPrincipal;
    private String historiaDoencaAtual;
    private String encaminhamentos;
    private String antecedentesFamiliares;
    private String habitosDeVida;
    private String observacoesGerais;

    private String alergias;
    private String cirurgias;
    private String medicamentosUsados;
    private String historicoDeInternacao;
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

    private String atitudeDesmame;
    private String habitosAlimentares;

    private String comportamentoEscola;
    private Boolean gostaEstudar;
    private String historicoEscolar;
    private Boolean gostaEscola;
    private String queixasComportamento;
    private Boolean dificuldadeAprendizagem;

    private String prefereBrincar;
    private String ativoOuPassivo;

    private String observacoesComportamentoSocial;
    private String observacoesEstrategicas;
    private String projetoTerapeuticoSingular;

    public static AnamneseDto fromEntity(Anamnese anamnese) {
    AnamneseDto dto = new AnamneseDto();
    dto.setId(anamnese.getId());
    dto.setPacienteId(anamnese.getPaciente().getId());
    dto.setDataRegistro(anamnese.getDataRegistro());
    dto.setProfissionalResponsavel(anamnese.getProfissionalResponsavel());
    dto.setQueixaPrincipal(anamnese.getQueixaPrincipal());
    dto.setHistoriaDoencaAtual(anamnese.getHistoriaDoencaAtual());
    dto.setEncaminhamentos(anamnese.getEncaminhamentos());
    dto.setAntecedentesFamiliares(anamnese.getAntecedentesFamiliares());
    dto.setHabitosDeVida(anamnese.getHabitosDeVida());
    dto.setObservacoesGerais(anamnese.getObservacoesGerais());
    dto.setAlergias(anamnese.getAlergias());
    dto.setCirurgias(anamnese.getCirurgias());
    dto.setMedicamentosUsados(anamnese.getMedicamentosUsados());
    dto.setHistoricoDeInternacao(anamnese.getHistoricoDeInternacao());
    dto.setObservacoesGestacao(anamnese.getObservacoesGestacao());
    dto.setLocalNascimento(anamnese.getLocalNascimento());
    dto.setTipoParto(anamnese.getTipoParto());
    dto.setPosicaoNascimento(anamnese.getPosicaoNascimento());
    dto.setChorouAoNascer(anamnese.getChorouAoNascer());
    dto.setNasceuVermelho(anamnese.getNasceuVermelho());
    dto.setNasceuRoxo(anamnese.getNasceuRoxo());
    dto.setAnoxia(anamnese.getAnoxia());
    dto.setIctericia(anamnese.getIctericia());
    dto.setPrecisouOxigenio(anamnese.getPrecisouOxigenio());
    dto.setIncubadora(anamnese.getIncubadora());
    dto.setAltaHospitalar(anamnese.getAltaHospitalar());
    return dto;
}
}