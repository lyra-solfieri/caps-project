package com.lyra.caps.service;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.lyra.caps.dto.RaasAcaoDto;
import com.lyra.caps.dto.RaasPageResponse;
import com.lyra.caps.dto.RaasRequestDTO;
import com.lyra.caps.dto.RaasResponseDTO;
import com.lyra.caps.model.Atendimento;
import com.lyra.caps.model.Endereco;
import com.lyra.caps.model.Paciente;
import com.lyra.caps.model.Raas;
import com.lyra.caps.model.RaasAcao;
import com.lyra.caps.model.enums.AcaoAuditoria;
import com.lyra.caps.model.enums.Raca;
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
    public RaasResponseDTO gerarRaas(RaasRequestDTO dto) {


        Paciente paciente = pacienteRepository.findById(dto.getPacienteId())
            .orElseThrow(() -> new EntityNotFoundException("Paciente não encontrado"));

        Atendimento atendimento = resolverAtendimento(dto, paciente);

        Raas raas = new Raas();

        raas.setNomeEstabelecimento(dto.getNomeEstabelecimento());
        raas.setCnes(dto.getCnes());


        raas.setPaciente(paciente);
        raas.setAtendimento(atendimento);


        raas.setNomePaciente(paciente.getNome());
        raas.setCartaoSus(paciente.getNumeroCartaoSUS());
        raas.setDataNascimento(paciente.getDataNascimento() != null ? paciente.getDataNascimento().toString() : null);
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
            raas.setAcoes(mapearAcoes(dto.getAcoes(), raas));
        }

        Raas raasSalvo = raasRepository.save(raas);

        auditoriaService.registrar(userService.getCurrentUser().getEmail(), AcaoAuditoria.CRIACAO, "Raas", raasSalvo.getId(),
                "RAAS gerado para paciente: " + paciente.getNome() + " - estabelecimento: " + dto.getNomeEstabelecimento(), null);

        return RaasResponseDTO.fromEntity(raasSalvo);
    }

    @Override
    public RaasResponseDTO atualizar(Long id, RaasRequestDTO dto) {

        Raas raas = raasRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("RAAS não encontrado"));

        Long pacienteId = dto.getPacienteId() != null ? dto.getPacienteId()
                : (raas.getPaciente() != null ? raas.getPaciente().getId() : null);

        Paciente paciente = pacienteRepository.findById(pacienteId)
            .orElseThrow(() -> new EntityNotFoundException("Paciente não encontrado"));

        Atendimento atendimento = resolverAtendimento(dto, paciente);

        raas.setNomeEstabelecimento(dto.getNomeEstabelecimento());
        raas.setCnes(dto.getCnes());
        raas.setPaciente(paciente);
        raas.setAtendimento(atendimento);

        raas.setNomePaciente(paciente.getNome());
        raas.setCartaoSus(paciente.getNumeroCartaoSUS());
        raas.setDataNascimento(paciente.getDataNascimento() != null ? paciente.getDataNascimento().toString() : null);
        raas.setNomeMae(paciente.getNomeMae());
        raas.setSexo(paciente.getGenero() != null ? paciente.getGenero().name() : null);

        raas.setDataAdmissao(atendimento.getDataAdmissao());
        raas.setDataConclusao(atendimento.getDataConclusao());
        raas.setMesAtendimento(atendimento.getMesAtendimento());
        raas.setNumeroAutorizacao(atendimento.getNumeroAutorizacao());

        if (dto.getAcoes() != null) {
            List<RaasAcao> novas = mapearAcoes(dto.getAcoes(), raas);
            raas.getAcoes().clear();
            raas.getAcoes().addAll(novas);
        } else {
            raas.getAcoes().clear();
        }

        Raas raasSalvo = raasRepository.save(raas);

        auditoriaService.registrar(userService.getCurrentUser().getEmail(), AcaoAuditoria.ATUALIZACAO, "Raas", raasSalvo.getId(),
                "RAAS atualizado para paciente: " + paciente.getNome() + " - estabelecimento: " + dto.getNomeEstabelecimento(), null);

        return RaasResponseDTO.fromEntity(raasSalvo);
    }

    private Atendimento resolverAtendimento(RaasRequestDTO dto, Paciente paciente) {
        if (dto.getAtendimentoId() != null) {
            Atendimento atendimento = atendimentoRepository.findById(dto.getAtendimentoId())
                .orElseThrow(() -> new EntityNotFoundException("Atendimento não encontrado"));

            if (!atendimento.getPaciente().getId().equals(paciente.getId())) {
                throw new IllegalArgumentException("Atendimento não pertence ao paciente");
            }
            return atendimento;
        }
        return criarAtendimento(dto, paciente);
    }

    private List<RaasAcao> mapearAcoes(List<RaasAcaoDto> acoesDto, Raas raas) {
        return acoesDto.stream().map(a -> {
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
    }

    private Atendimento criarAtendimento(RaasRequestDTO dto, Paciente paciente) {
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

    @Override
    public RaasResponseDTO buscarPorId(Long id) {
        return raasRepository.findById(id)
            .map(RaasResponseDTO::fromEntity)
            .orElseThrow(() -> new EntityNotFoundException("RAAS não encontrado"));
    }

    @Override
    public List<RaasResponseDTO> listarPorPaciente(Long pacienteId) {

        try {
            return raasRepository.findByPacienteId(pacienteId).stream()
                .map(RaasResponseDTO::fromEntity)
                .toList();
        } catch (Exception e) {
            throw new RuntimeException("Paciente nao encontrado");
        }
    }

    @Override
    public RaasPageResponse listarPaginado(Pageable pageable) {
        var page = raasRepository.findAll(pageable);
        List<RaasResponseDTO> content = page.getContent().stream()
                .map(RaasResponseDTO::fromEntity)
                .toList();
        return new RaasPageResponse(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast());
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

        Paciente paciente = raas.getPaciente();
        Atendimento atendimento = raas.getAtendimento();
        Endereco endereco = paciente != null ? paciente.getEndereco() : null;

        Map<String, Object> params = new HashMap<>();

        params.put("nomeEstabelecimento", safe(raas.getNomeEstabelecimento()));
        params.put("cnes", safe(raas.getCnes()));

        params.put("numeroProntuario", safe(paciente != null ? paciente.getNumeroProntuario() : null));
        params.put("nomePaciente", safe(raas.getNomePaciente()));
        params.put("cartaoSus", safe(raas.getCartaoSus()));
        params.put("sexo", formatarSexo(raas.getSexo()));
        params.put("dataNascimento", formatarDataTexto(raas.getDataNascimento()));
        params.put("racaCor", formatarRaca(paciente != null ? paciente.getRaca() : null));
        params.put("nacionalidade", safe(paciente != null ? paciente.getNacionalidade() : null));
        params.put("etniaIndigena", safe(paciente != null ? paciente.getEtniaIndigena() : null));
        params.put("nomeMae", safe(raas.getNomeMae()));
        params.put("nomeResponsavel", safe(paciente != null ? paciente.getResponsavelLegal() : null));

        if (endereco != null) {
            String logradouroNumero = (safe(endereco.getLogradouro()) + " "
                    + safe(endereco.getNumero())).trim();
            params.put("endereco", logradouroNumero);
            params.put("complemento", safe(endereco.getComplemento()));
            params.put("municipio", safe(endereco.getCidade()));
            params.put("uf", safe(endereco.getEstado()));
            params.put("cep", safe(endereco.getCep()));
        } else {
            params.put("endereco", "");
            params.put("complemento", "");
            params.put("municipio", "");
            params.put("uf", "");
            params.put("cep", "");
        }
        params.put("codigoIbge", safe(paciente != null ? paciente.getCodIBGEMunicipio() : null));
        params.put("telefoneCelular", safe(paciente != null ? paciente.getTelefone() : null));
        params.put("telefoneContato", "");

        params.put("dataAdmissao", formatarData(raas.getDataAdmissao()));
        params.put("dataConclusao", formatarData(raas.getDataConclusao()));
        params.put("mesAtendimento", safe(raas.getMesAtendimento()));
        params.put("numeroAutorizacao", raas.getNumeroAutorizacao() != null ? raas.getNumeroAutorizacao().toString() : "");

        boolean usoDrogasSim = atendimento != null && "SIM".equalsIgnoreCase(atendimento.getUsoDrogas());
        params.put("usoDrogasSim", marcar(usoDrogasSim));
        params.put("usoDrogasNao", marcar(!usoDrogasSim));
        params.put("alcool", marcar(atendimento != null && atendimento.isAlcool()));
        params.put("crack", marcar(atendimento != null && atendimento.isCrack()));
        params.put("outrasDrogas", marcar(atendimento != null && atendimento.isOutrasDrogas()));

        params.put("origemDemanda", marcar(atendimento != null && atendimento.isOrigemDemandaEspontanea()));
        params.put("origemAtencaoBasica", marcar(atendimento != null && atendimento.isOrigemAtencaoBasica()));
        params.put("origemUrgencia", marcar(atendimento != null && atendimento.isOrigemUrgencia()));
        params.put("origemCaps", marcar(atendimento != null && atendimento.isOrigemCaps()));
        params.put("origemHospital", marcar(atendimento != null && atendimento.isOrigemHospitalGeral()));
        params.put("origemPsiquiatrico", marcar(atendimento != null && atendimento.isOrigemHospitalPsiquiatrico()));

        params.put("cidPrincipal", safe(atendimento != null ? atendimento.getCidPrincipal() : null));
        params.put("descricaoCidPrincipal", safe(atendimento != null ? atendimento.getDescricaoCidPrincipal() : null));
        params.put("cidSecundario", safe(atendimento != null ? atendimento.getCidSecundario() : null));
        params.put("descricaoCidSecundario", safe(atendimento != null ? atendimento.getDescricaoCidSecundario() : null));

        boolean esfSim = atendimento != null && "SIM".equalsIgnoreCase(atendimento.getEsf());
        params.put("esfSim", marcar(esfSim));
        params.put("esfNao", marcar(!esfSim));
        params.put("cnesAtendimento", safe(atendimento != null ? atendimento.getCnesAtendimento() : null));

        params.put("encaminhamentoCaps", marcar(atendimento != null && atendimento.isEncaminhamentoCaps()));
        params.put("encaminhamentoAB", marcar(atendimento != null && atendimento.isEncaminhamentoAtencaoBasica()));
        params.put("alta", marcar(atendimento != null && atendimento.isAlta()));
        params.put("obito", marcar(atendimento != null && atendimento.isObito()));

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

    private static String marcar(boolean valor) {
        return valor ? "X" : "";
    }

    private static String formatarSexo(String sexo) {
        if (sexo == null || sexo.isBlank()) {
            return "";
        }
        return switch (sexo.trim().toUpperCase()) {
            case "MASCULINO", "M" -> "M";
            case "FEMININO", "F" -> "F";
            default -> "";
        };
    }

    private static String formatarRaca(Raca raca) {
        if (raca == null) {
            return "";
        }
        return switch (raca) {
            case BRANCA -> "Branca";
            case PRETA -> "Preta";
            case PARDA -> "Parda";
            case AMARELA -> "Amarela";
            case INDIGENA -> "Indígena";
            case NAO_DECLARADO -> "Não declarado";
        };
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
