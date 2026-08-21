package com.lyra.caps.service;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.lyra.caps.dto.RaasRequestDTO;
import com.lyra.caps.model.Atendimento;
import com.lyra.caps.model.Paciente;
import com.lyra.caps.model.Raas;
import com.lyra.caps.model.RaasAcao;
import com.lyra.caps.model.enums.AcaoAuditoria;
import com.lyra.caps.repository.AtendimentoRepository;
import com.lyra.caps.repository.PacienteRepository;
import com.lyra.caps.repository.RaasRepository;

import jakarta.persistence.EntityNotFoundException;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service
public class RaasService implements IRaasService {

    private static final DateTimeFormatter DATA_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final String TEMPLATE_PATH = "/reports/raas.jrxml";

    private final RaasRepository raasRepository;
    private final PacienteRepository pacienteRepository;
    private final AtendimentoRepository atendimentoRepository;
    private final AuditoriaService auditoriaService;
    private final UserService userService;

    private JasperReport templateCache;

    public RaasService(RaasRepository raasRepository, PacienteRepository pacienteRepository,
            AtendimentoRepository atendimentoRepository, AuditoriaService auditoriaService,
            UserService userService) {

        this.raasRepository = raasRepository;
        this.pacienteRepository = pacienteRepository;
        this.atendimentoRepository = atendimentoRepository;
        this.auditoriaService = auditoriaService;
        this.userService = userService;

    }

    @Override
    public Raas gerarRaas(RaasRequestDTO dto) {


        Paciente paciente = pacienteRepository.findById(dto.getPacienteId())
            .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));

        Atendimento atendimento = atendimentoRepository.findById(dto.getAtendimentoId())
            .orElseThrow(() -> new RuntimeException("Atendimento não encontrado"));


        if (!atendimento.getPaciente().getId().equals(paciente.getId())) {
            throw new RuntimeException("Atendimento não pertence ao paciente");
        }

        Raas raas = new Raas();

        raas.setNomeEstabelecimento(dto.getNomeEstabelecimento());
        raas.setCnes(dto.getCnes());


        raas.setPaciente(paciente);
        raas.setAtendimento(atendimento);


        raas.setNomePaciente(paciente.getNome());
        raas.setCartaoSus(paciente.getNumeroCartaoSUS());
        raas.setDataNascimento(paciente.getDataNascimento().toString());
        raas.setNomeMae(paciente.getNomeMae());
        raas.setSexo(paciente.getGenero() != null ? paciente.getGenero().name() : null);


        raas.setDataAdmissao(atendimento.getDataAdmissao());
        raas.setDataConclusao(atendimento.getDataConclusao());
        raas.setMesAtendimento(atendimento.getMesAtendimento());
        raas.setNumeroAutorizacao(atendimento.getNumeroAutorizacao());

        raas.setStatus(true);
        raas.setEnviadoSUS(false);
        raas.setDataGeracao(LocalDate.now());

        // raas.setCidPrincipal(atendimento.getCidPrincipal());
        // raas.setDescricaoCidPrincipal(atendimento.getDescricaoCidPrincipal());

        // raas.setCidSecundario(atendimento.getCidSecundario());
        // raas.setDescricaoCidSecundario(atendimento.getDescricaoCidSecundario());

        if (dto.getAcoes() != null) {
            List<RaasAcao> acoes = dto.getAcoes().stream().map(a -> {
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
                return acao;
            }).toList();

            raas.setAcoes(acoes);
        }

        Raas raasSalvo = raasRepository.save(raas);

        auditoriaService.registrar(userService.getCurrentUser().getEmail(), AcaoAuditoria.CRIACAO, "Raas", raasSalvo.getId(),
                "RAAS gerado para paciente: " + paciente.getNome() + " - estabelecimento: " + dto.getNomeEstabelecimento(), null);

        return raasSalvo;
    }

    @Override
    public Raas buscarPorId(Long id) {
        return raasRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("RAAS não encontrado"));
    }

    @Override
    public List<Raas> listarPorPaciente(Long pacienteId) {

        try {
            return raasRepository.findByPacienteId(pacienteId);
        } catch (Exception e) {
            throw new RuntimeException("Paciente nao encontrado");
        }
    }

    @Override
    public void deletar(Long id) {
        try {
            raasRepository.deleteById(id);

            auditoriaService.registrar(userService.getCurrentUser().getEmail(), AcaoAuditoria.EXCLUSAO, "Raas", id,
                    "RAAS excluido - ID: " + id, null);
        } catch (Exception e) {
            throw new RuntimeException("Raas nao encontrado");
        }
    }

    @Override
    public byte[] gerarPdf(Long raasId) {

        Raas raas = raasRepository.findById(raasId)
            .orElseThrow(() -> new EntityNotFoundException("RAAS não encontrado"));

        Map<String, Object> params = new HashMap<>();

        params.put("nomeEstabelecimento", safe(raas.getNomeEstabelecimento()));
        params.put("cnes", safe(raas.getCnes()));

        params.put("nomePaciente", safe(raas.getNomePaciente()));
        params.put("cartaoSus", safe(raas.getCartaoSus()));
        params.put("sexo", safe(raas.getSexo()));
        params.put("dataNascimento", formatarDataTexto(raas.getDataNascimento()));
        params.put("nomeMae", safe(raas.getNomeMae()));

        params.put("dataAdmissao", formatarData(raas.getDataAdmissao()));
        params.put("dataConclusao", formatarData(raas.getDataConclusao()));
        params.put("mesAtendimento", safe(raas.getMesAtendimento()));
        params.put("numeroAutorizacao", raas.getNumeroAutorizacao() != null ? raas.getNumeroAutorizacao().toString() : "");

        List<RaasAcao> acoes = raas.getAcoes() != null ? raas.getAcoes() : List.of();
        params.put("acoesDataset", new JRBeanCollectionDataSource(acoes));

        try {
            JasperPrint jasperPrint = JasperFillManager.fillReport(template(), params, new JREmptyDataSource());
            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (JRException e) {
            throw new RuntimeException("Erro ao gerar PDF do RAAS", e);
        }
    }

    private JasperReport template() {
        if (templateCache == null) {
            synchronized (this) {
                if (templateCache == null) {
                    try (InputStream reportStream = getClass().getResourceAsStream(TEMPLATE_PATH)) {
                        if (reportStream == null) {
                            throw new IllegalStateException("Template RAAS não encontrado em " + TEMPLATE_PATH);
                        }
                        templateCache = JasperCompileManager.compileReport(reportStream);
                    } catch (JRException | java.io.IOException e) {
                        throw new RuntimeException("Erro ao compilar template do RAAS", e);
                    }
                }
            }
        }
        return templateCache;
    }

    private static String safe(String valor) {
        return valor != null ? valor : "";
    }

    private static String formatarData(LocalDate data) {
        return data != null ? data.format(DATA_FORMATTER) : "";
    }

    private static String formatarDataTexto(String textoIso) {
        if (textoIso == null || textoIso.isBlank()) {
            return "";
        }
        try {
            return LocalDate.parse(textoIso).format(DATA_FORMATTER);
        } catch (DateTimeParseException e) {
            return textoIso;
        }
    }
}
