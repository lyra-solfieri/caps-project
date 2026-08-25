package com.lyra.caps.dto;

import java.time.LocalDateTime;

import com.lyra.caps.model.Anamnese;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AnamneseDto {

    private Long id;

    @NotNull(message = "Paciente é obrigatório")
    private Long pacienteId;

    private LocalDateTime dataRegistro;

    @NotBlank(message = "Profissional responsável é obrigatório")
    @Size(min = 3, max = 150, message = "Profissional responsável deve ter entre 3 e 150 caracteres")
    private String profissionalResponsavel;

    @NotBlank(message = "Queixa principal é obrigatória")
    @Size(min = 3, max = 2000, message = "Queixa principal deve ter entre 3 e 2000 caracteres")
    private String queixaPrincipal;

    @Size(max = 2000, message = "História da doença atual deve ter no máximo 2000 caracteres")
    private String historiaDoencaAtual;

    @Size(max = 1000, message = "Encaminhamentos deve ter no máximo 1000 caracteres")
    private String encaminhamentos;

    @Size(max = 1000, message = "Antecedentes familiares deve ter no máximo 1000 caracteres")
    private String antecedentesFamiliares;

    @Size(max = 1000, message = "Hábitos de vida deve ter no máximo 1000 caracteres")
    private String habitosDeVida;

    @Size(max = 1000, message = "Observações gerais deve ter no máximo 1000 caracteres")
    private String observacoesGerais;

    @Size(max = 200, message = "Alergias deve ter no máximo 200 caracteres")
    private String alergias;

    @Size(max = 200, message = "Cirurgias deve ter no máximo 200 caracteres")
    private String cirurgias;

    @Size(max = 1000, message = "Medicamentos usados deve ter no máximo 1000 caracteres")
    private String medicamentosUsados;

    @Size(max = 1000, message = "Histórico de internação deve ter no máximo 1000 caracteres")
    private String historicoDeInternacao;

    @Size(max = 1000, message = "Observações da gestação deve ter no máximo 1000 caracteres")
    private String observacoesGestacao;

    @Size(max = 100, message = "Local de nascimento deve ter no máximo 100 caracteres")
    private String localNascimento;

    @Size(max = 100, message = "Tipo de parto deve ter no máximo 100 caracteres")
    private String tipoParto;

    @Size(max = 100, message = "Posição ao nascer deve ter no máximo 100 caracteres")
    private String posicaoNascimento;

    private Boolean chorouAoNascer;
    private Boolean nasceuVermelho;
    private Boolean nasceuRoxo;
    private Boolean anoxia;
    private Boolean ictericia;
    private Boolean precisouOxigenio;
    private Boolean incubadora;

    @Size(max = 100, message = "Alta hospitalar deve ter no máximo 100 caracteres")
    private String altaHospitalar;

    private Boolean sorriu;
    private Boolean equilibrouPescoco;
    private Boolean engatinhou;
    private Boolean sentou;
    private Boolean andou;

    @Size(max = 50, message = "Primeiras palavras deve ter no máximo 50 caracteres")
    private String primeirasPalavras;

    private Boolean falaCorreta;
    private Boolean trocouLetras;
    private Boolean gaguejou;
    private Boolean denticao;

    @Size(max = 100, message = "Controle esfincteriano anal diurno deve ter no máximo 100 caracteres")
    private String controleEsfincterAnalDiurno;

    @Size(max = 100, message = "Controle esfincteriano vesical diurno deve ter no máximo 100 caracteres")
    private String controleEsfincterVesicalDiurno;

    @Size(max = 100, message = "Controle esfincteriano noturno deve ter no máximo 100 caracteres")
    private String controleEsfincterNoturno;

    @Size(max = 200, message = "Cuidados por quem deve ter no máximo 200 caracteres")
    private String cuidadosPorQuem;

    private Boolean usouChupeta;
    private Boolean chupouDedo;
    private Boolean roeUnhas;
    private Boolean puxaOrelha;
    private Boolean arrancaCabelos;
    private Boolean mordeLabios;
    private Boolean tiques;

    @Size(max = 1000, message = "Atitude diante dos hábitos deve ter no máximo 1000 caracteres")
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

    @Size(max = 1000, message = "Atitude no desmame deve ter no máximo 1000 caracteres")
    private String atitudeDesmame;

    @Size(max = 1000, message = "Hábitos alimentares deve ter no máximo 1000 caracteres")
    private String habitosAlimentares;

    @Size(max = 1000, message = "Comportamento na escola deve ter no máximo 1000 caracteres")
    private String comportamentoEscola;

    private Boolean gostaEstudar;
    private Boolean gostaEscola;
    private Boolean dificuldadeAprendizagem;

    @Size(max = 1000, message = "Histórico escolar deve ter no máximo 1000 caracteres")
    private String historicoEscolar;

    @Size(max = 1000, message = "Queixas de comportamento deve ter no máximo 1000 caracteres")
    private String queixasComportamento;

    @Size(max = 100, message = "Prefere brincar deve ter no máximo 100 caracteres")
    private String prefereBrincar;

    @Size(max = 100, message = "Ativo ou passivo deve ter no máximo 100 caracteres")
    private String ativoOuPassivo;

    @Size(max = 1000, message = "Observações do comportamento social deve ter no máximo 1000 caracteres")
    private String observacoesComportamentoSocial;

    @Size(max = 1000, message = "Observações estratégicas deve ter no máximo 1000 caracteres")
    private String observacoesEstrategicas;

    @Size(max = 1000, message = "Projeto terapêutico singular deve ter no máximo 1000 caracteres")
    private String projetoTerapeuticoSingular;

    public static AnamneseDto fromEntity(Anamnese anamnese) {
        if (anamnese == null) {
            return null;
        }
        
        AnamneseDto dto = new AnamneseDto();
        dto.setId(anamnese.getId());
        dto.setPacienteId(anamnese.getPaciente() != null ? anamnese.getPaciente().getId() : null);
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
        dto.setSorriu(anamnese.getSorriu());
        dto.setEquilibrouPescoco(anamnese.getEquilibrouPescoco());
        dto.setEngatinhou(anamnese.getEngatinhou());
        dto.setSentou(anamnese.getSentou());
        dto.setAndou(anamnese.getAndou());
        dto.setPrimeirasPalavras(anamnese.getPrimeirasPalavras());
        dto.setFalaCorreta(anamnese.getFalaCorreta());
        dto.setTrocouLetras(anamnese.getTrocouLetras());
        dto.setGaguejou(anamnese.getGaguejou());
        dto.setDenticao(anamnese.getDenticao());
        dto.setControleEsfincterAnalDiurno(anamnese.getControleEsfincterAnalDiurno());
        dto.setControleEsfincterVesicalDiurno(anamnese.getControleEsfincterVesicalDiurno());
        dto.setControleEsfincterNoturno(anamnese.getControleEsfincterNoturno());
        dto.setCuidadosPorQuem(anamnese.getCuidadosPorQuem());
        dto.setUsouChupeta(anamnese.getUsouChupeta());
        dto.setChupouDedo(anamnese.getChupouDedo());
        dto.setRoeUnhas(anamnese.getRoeUnhas());
        dto.setPuxaOrelha(anamnese.getPuxaOrelha());
        dto.setArrancaCabelos(anamnese.getArrancaCabelos());
        dto.setMordeLabios(anamnese.getMordeLabios());
        dto.setTiques(anamnese.getTiques());
        dto.setAtitudeDianteHabitos(anamnese.getAtitudeDianteHabitos());
        dto.setDormeBem(anamnese.getDormeBem());
        dto.setPulaQuandoDorme(anamnese.getPulaQuandoDorme());
        dto.setBabaNoite(anamnese.getBabaNoite());
        dto.setSudorese(anamnese.getSudorese());
        dto.setAcordaVariasVezes(anamnese.getAcordaVariasVezes());
        dto.setFalaDormindo(anamnese.getFalaDormindo());
        dto.setGritaDormindo(anamnese.getGritaDormindo());
        dto.setRangeDentes(anamnese.getRangeDentes());
        dto.setSonambulo(anamnese.getSonambulo());
        dto.setPesadelos(anamnese.getPesadelos());
        dto.setAmamentadoNoPeito(anamnese.getAmamentadoNoPeito());
        dto.setAtitudeDesmame(anamnese.getAtitudeDesmame());
        dto.setHabitosAlimentares(anamnese.getHabitosAlimentares());
        dto.setComportamentoEscola(anamnese.getComportamentoEscola());
        dto.setGostaEstudar(anamnese.getGostaEstudar());
        dto.setHistoricoEscolar(anamnese.getHistoricoEscolar());
        dto.setGostaEscola(anamnese.getGostaEscola());
        dto.setQueixasComportamento(anamnese.getQueixasComportamento());
        dto.setDificuldadeAprendizagem(anamnese.getDificuldadeAprendizagem());
        dto.setPrefereBrincar(anamnese.getPrefereBrincar());
        dto.setAtivoOuPassivo(anamnese.getAtivoOuPassivo());
        dto.setObservacoesComportamentoSocial(anamnese.getObservacoesComportamentoSocial());
        dto.setObservacoesEstrategicas(anamnese.getObservacoesEstrategicas());
        dto.setProjetoTerapeuticoSingular(anamnese.getProjetoTerapeuticoSingular());
        
        return dto;
    }
}
