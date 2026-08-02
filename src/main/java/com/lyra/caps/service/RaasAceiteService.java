package com.lyra.caps.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lyra.caps.dto.RaasAceitarDTO;
import com.lyra.caps.dto.RaasAcaoDto;
import com.lyra.caps.model.Atendimento;
import com.lyra.caps.model.Endereco;
import com.lyra.caps.model.OcrProcessamento;
import com.lyra.caps.model.Paciente;
import com.lyra.caps.model.Raas;
import com.lyra.caps.model.RaasAcao;
import com.lyra.caps.model.enums.Genero;
import com.lyra.caps.model.enums.OcrStatus;
import com.lyra.caps.model.enums.Raca;
import com.lyra.caps.repository.AtendimentoRepository;
import com.lyra.caps.repository.OcrProcessamentoRepository;
import com.lyra.caps.repository.PacienteRepository;
import com.lyra.caps.repository.RaasRepository;
import com.lyra.caps.util.LogPersonalizado;

/**
 * Aplica os dados revisados no front ao cadastro, criando Paciente (se
 * necessário), Atendimento e RAAS somente quando o usuário aceita o
 * resultado do OCR. Nada é persistido no momento do callback do OCR.
 */
@Service
public class RaasAceiteService {

    private final OcrProcessamentoRepository processamentoRepository;
    private final PacienteRepository pacienteRepository;
    private final AtendimentoRepository atendimentoRepository;
    private final RaasRepository raasRepository;

    public RaasAceiteService(OcrProcessamentoRepository processamentoRepository,
                             PacienteRepository pacienteRepository,
                             AtendimentoRepository atendimentoRepository,
                             RaasRepository raasRepository) {
        this.processamentoRepository = processamentoRepository;
        this.pacienteRepository = pacienteRepository;
        this.atendimentoRepository = atendimentoRepository;
        this.raasRepository = raasRepository;
    }

    @Transactional
    public Raas aceitar(RaasAceitarDTO dto) {
        var log = LogPersonalizado.getLogger(this.getClass());

        if (dto == null || !hasText(dto.getCorrelationId())) {
            throw new IllegalArgumentException("correlationId é obrigatório");
        }

        OcrProcessamento processamento = processamentoRepository
                .findByCorrelationId(dto.getCorrelationId())
                .orElseThrow(() -> new IllegalArgumentException("Processamento OCR não encontrado"));

        if (processamento.getStatus() == OcrStatus.EM_PROCESSAMENTO
                || processamento.getStatus() == OcrStatus.FALHA) {
            throw new IllegalStateException(
                    "Processamento OCR ainda não está pronto para aceite: " + processamento.getStatus());
        }
        if (processamento.getRaasCriadoId() != null) {
            throw new IllegalStateException("RAAS já foi aceito para este processamento");
        }

        Paciente paciente = resolverPaciente(dto);
        Atendimento atendimento = criarAtendimento(dto, paciente);
        Raas raas = criarRaas(dto, paciente, atendimento);

        processamento.setPacienteCriadoId(paciente.getId());
        processamento.setAtendimentoCriadoId(atendimento.getId());
        processamento.setRaasCriadoId(raas.getId());
        processamentoRepository.save(processamento);

        log.info(String.format(
                "RAAS aceito a partir de OCR: correlationId=%s, paciente=%s, atendimento=%s, raas=%s",
                processamento.getCorrelationId(), paciente.getId(), atendimento.getId(), raas.getId()));

        return raas;
    }

    // ------------------------------------------------------------------
    // Paciente
    // ------------------------------------------------------------------

    private Paciente resolverPaciente(RaasAceitarDTO dto) {
        if (hasText(dto.getCartaoSus())) {
            Optional<Paciente> existente = pacienteRepository.findByNumeroCartaoSUS(dto.getCartaoSus());
            if (existente.isPresent()) {
                Paciente paciente = existente.get();
                aplicarDadosPaciente(paciente, dto);
                return paciente;
            }
        }

        Paciente paciente = new Paciente();
        aplicarDadosPaciente(paciente, dto);
        return pacienteRepository.save(paciente);
    }

    private void aplicarDadosPaciente(Paciente paciente, RaasAceitarDTO dto) {
        setIfPresent(paciente.getNome(), () -> paciente.setNome(dto.getNomePaciente()));
        setIfPresent(paciente.getNumeroCartaoSUS(), () -> paciente.setNumeroCartaoSUS(dto.getCartaoSus()));
        setIfPresent(paciente.getNumeroProntuario(), () -> paciente.setNumeroProntuario(dto.getNumeroProntuario()));
        setIfPresent(paciente.getGenero(), () -> paciente.setGenero(asGenero(dto.getSexo())));
        setIfPresent(paciente.getDataNascimento(), () -> paciente.setDataNascimento(dto.getDataNascimento()));
        setIfPresent(paciente.getNacionalidade(), () -> paciente.setNacionalidade(dto.getNacionalidade()));
        setIfPresent(paciente.getRaca(), () -> paciente.setRaca(asRaca(dto.getRacaCor())));
        setIfPresent(paciente.getEtniaIndigena(), () -> paciente.setEtniaIndigena(dto.getEtniaIndigena()));
        setIfPresent(paciente.getNomeMae(), () -> paciente.setNomeMae(dto.getNomeMae()));
        setIfPresent(paciente.getResponsavelLegal(), () -> paciente.setResponsavelLegal(dto.getNomeResponsavel()));
        setIfPresent(paciente.getCodIBGEMunicipio(), () -> paciente.setCodIBGEMunicipio(dto.getCodigoIbge()));
        setIfPresent(paciente.getTelefone(), () -> paciente.setTelefone(dto.getTelefoneCelular()));
        setIfPresent(paciente.getTelefoneResponsavelLegal(),
                () -> paciente.setTelefoneResponsavelLegal(dto.getTelefoneContato()));
        aplicarEndereco(paciente, dto);
    }

    private void aplicarEndereco(Paciente paciente, RaasAceitarDTO dto) {
        if (paciente.getEndereco() == null) {
            if (!temEndereco(dto)) {
                return;
            }
            Endereco endereco = new Endereco();
            endereco.setCep(dto.getCep());
            endereco.setLogradouro(dto.getEndereco());
            endereco.setComplemento(dto.getComplemento());
            endereco.setCidade(dto.getMunicipio());
            endereco.setEstado(dto.getUf());
            paciente.setEndereco(endereco);
            return;
        }

        Endereco endereco = paciente.getEndereco();
        setIfPresent(endereco.getCep(), () -> endereco.setCep(dto.getCep()));
        setIfPresent(endereco.getLogradouro(), () -> endereco.setLogradouro(dto.getEndereco()));
        setIfPresent(endereco.getComplemento(), () -> endereco.setComplemento(dto.getComplemento()));
        setIfPresent(endereco.getCidade(), () -> endereco.setCidade(dto.getMunicipio()));
        setIfPresent(endereco.getEstado(), () -> endereco.setEstado(dto.getUf()));
    }

    // ------------------------------------------------------------------
    // Atendimento
    // ------------------------------------------------------------------

    private Atendimento criarAtendimento(RaasAceitarDTO dto, Paciente paciente) {
        Atendimento atendimento = new Atendimento();
        atendimento.setPaciente(paciente);
        atendimento.setDataAdmissao(dto.getDataAdmissao());
        atendimento.setDataConclusao(dto.getDataConclusao());
        atendimento.setMesAtendimento(dto.getMesAtendimento());
        atendimento.setNumeroAutorizacao(dto.getNumeroAutorizacao());

        atendimento.setUsoDrogas(dto.getUsoDrogas());
        atendimento.setAlcool(dto.isAlcool());
        atendimento.setCrack(dto.isCrack());
        atendimento.setOutrasDrogas(dto.isOutrasDrogas());

        atendimento.setOrigemDemandaEspontanea(dto.isOrigemDemanda());
        atendimento.setOrigemAtencaoBasica(dto.isOrigemAtencaoBasica());
        atendimento.setOrigemUrgencia(dto.isOrigemUrgencia());
        atendimento.setOrigemCaps(dto.isOrigemCaps());
        atendimento.setOrigemHospitalGeral(dto.isOrigemHospital());
        atendimento.setOrigemHospitalPsiquiatrico(dto.isOrigemPsiquiatrico());

        atendimento.setCidPrincipal(dto.getCidPrincipal());
        atendimento.setDescricaoCidPrincipal(dto.getDescricaoCidPrincipal());
        atendimento.setCidSecundario(dto.getCidSecundario());
        atendimento.setDescricaoCidSecundario(dto.getDescricaoCidSecundario());

        atendimento.setEsf(dto.getEsf());
        atendimento.setCnesAtendimento(dto.getCnesAtendimento());

        atendimento.setEncaminhamentoCaps(dto.isEncaminhamentoCaps());
        atendimento.setEncaminhamentoAtencaoBasica(dto.isEncaminhamentoAB());
        atendimento.setAlta(dto.isAlta());
        atendimento.setObito(dto.isObito());

        return atendimentoRepository.save(atendimento);
    }

    // ------------------------------------------------------------------
    // RAAS
    // ------------------------------------------------------------------

    private Raas criarRaas(RaasAceitarDTO dto, Paciente paciente, Atendimento atendimento) {
        Raas raas = new Raas();
        raas.setNomeEstabelecimento(dto.getNomeEstabelecimento());
        raas.setCnes(dto.getCnes());
        raas.setPaciente(paciente);
        raas.setAtendimento(atendimento);
        raas.setNomePaciente(dto.getNomePaciente());
        raas.setCartaoSus(dto.getCartaoSus());
        raas.setSexo(nomeSexo(dto.getSexo()));
        raas.setDataNascimento(dto.getDataNascimento() != null ? dto.getDataNascimento().toString() : null);
        raas.setNomeMae(dto.getNomeMae());
        raas.setDataAdmissao(dto.getDataAdmissao());
        raas.setDataConclusao(dto.getDataConclusao());
        raas.setMesAtendimento(dto.getMesAtendimento());
        raas.setNumeroAutorizacao(dto.getNumeroAutorizacao());
        raas.setStatus(true);
        raas.setEnviadoSUS(false);
        raas.setDataGeracao(LocalDate.now());
        raas.setAcoes(mapearAcoes(dto.getAcoes(), raas));
        return raasRepository.save(raas);
    }

    private List<RaasAcao> mapearAcoes(List<RaasAcaoDto> acoesDto, Raas raas) {
        List<RaasAcao> acoes = new ArrayList<>();
        if (acoesDto == null) {
            return acoes;
        }
        for (RaasAcaoDto a : acoesDto) {
            RaasAcao acao = new RaasAcao();
            acao.setCodigoAcao(a.getCodigoAcao());
            acao.setQuantidade(a.getQuantidade());
            acao.setData(a.getData());
            acao.setCid10(a.getCid10());
            acao.setServico(a.getServico());
            acao.setClassificacao(a.getClassificacao());
            acao.setCboExecutor(a.getCboExecutor());
            acao.setCartaoProfissional(a.getCartaoProfissional());
            acao.setCodigoEquipe(a.getCodigoEquipe());
            acao.setCaps(a.isCaps());
            acao.setTerritorio(a.isTerritorio());
            acao.setRaas(raas);
            acoes.add(acao);
        }
        return acoes;
    }

    // ------------------------------------------------------------------
    // Conversores e utilitários
    // ------------------------------------------------------------------

    private static void setIfPresent(Object current, Runnable setter) {
        if (current == null) {
            setter.run();
        }
    }

    private static boolean temEndereco(RaasAceitarDTO dto) {
        return hasText(dto.getCep())
                || hasText(dto.getEndereco())
                || hasText(dto.getComplemento())
                || hasText(dto.getMunicipio())
                || hasText(dto.getUf());
    }

    private static boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private static Genero asGenero(String value) {
        if (!hasText(value)) {
            return null;
        }
        String upper = value.toUpperCase().trim();
        if (upper.startsWith("M")) {
            return Genero.MASCULINO;
        }
        if (upper.startsWith("F")) {
            return Genero.FEMININO;
        }
        return Genero.OUTRO;
    }

    private static String nomeSexo(String value) {
        if (!hasText(value)) {
            return null;
        }
        String upper = value.toUpperCase().trim();
        if (upper.startsWith("M")) {
            return "MASCULINO";
        }
        if (upper.startsWith("F")) {
            return "FEMININO";
        }
        return upper;
    }

    private static Raca asRaca(String value) {
        if (!hasText(value)) {
            return null;
        }
        String upper = value.toUpperCase().replace("Í", "I").replace("Ã", "A").replace("Ç", "C");
        if (upper.contains("BRANC")) {
            return Raca.BRANCA;
        }
        if (upper.contains("PRET")) {
            return Raca.PRETA;
        }
        if (upper.contains("PARD")) {
            return Raca.PARDA;
        }
        if (upper.contains("AMAREL")) {
            return Raca.AMARELA;
        }
        if (upper.contains("INDIG")) {
            return Raca.INDIGENA;
        }
        return Raca.NAO_DECLARADO;
    }
}
