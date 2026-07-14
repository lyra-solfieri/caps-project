package com.lyra.caps.service;

import java.io.InputStream;
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

// import net.sf.jasperreports.engine.JREmptyDataSource;
// import net.sf.jasperreports.engine.JasperCompileManager;
// import net.sf.jasperreports.engine.JasperExportManager;
// import net.sf.jasperreports.engine.JasperFillManager;
// import net.sf.jasperreports.engine.JasperPrint;
// import net.sf.jasperreports.engine.JasperReport;
// import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service
public class RaasService implements IRaasService {

    private final RaasRepository raasRepository;
    private final PacienteRepository pacienteRepository;
    private final AtendimentoRepository atendimentoRepository;
    private final AuditoriaService auditoriaService;
    private final UserService userService;


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

        
        raas.setDataAdmissao(atendimento.getDataAdmissao());
        raas.setDataConclusao(atendimento.getDataConclusao());
        raas.setMesAtendimento(atendimento.getMesAtendimento());
        raas.setNumeroAutorizacao(atendimento.getNumeroAutorizacao());

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
                acao.setServico(a.getServico());
                acao.setClassificacao(a.getClassificacao());
                acao.setCboExecutor(a.getCboExecutor());
                acao.setCartaoProfissional(a.getCartaoProfissional());
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
            .orElseThrow(() -> new RuntimeException("RAAS não encontrado"));
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

//     try {

//         Raas raas = raasRepository.findById(raasId).orElseThrow(
//                  () -> new RuntimeException("Raas não encontrada") );

//         Map<String, Object> params = new HashMap<>();


//         params.put("nomeEstabelecimento", (raas.getNomeEstabelecimento()));
//         params.put("cnes", (raas.getCnes()));


//         params.put("nomePaciente", (raas.getNomePaciente()));
//         params.put("cartaoSus", raas.getCartaoSus());
//         params.put("dataNascimento", (raas.getDataNascimento()));
//         params.put("nomeMae", (raas.getNomeMae()));


//         params.put("dataAdmissao", raas.getDataAdmissao());
//         params.put("dataConclusao", raas.getDataConclusao());
//         params.put("mesAtendimento", (raas.getMesAtendimento()));
//         params.put("numeroAutorizacao", raas.getNumeroAutorizacao());

//         // params.put("cidPrincipal", safe(raas.getCidPrincipal()));
//         // params.put("descricaoCidPrincipal", safe(raas.getDescricaoCidPrincipal()));

//         // params.put("cidSecundario", safe(raas.getCidSecundario()));
//         // params.put("descricaoCidSecundario", safe(raas.getDescricaoCidSecundario()));


//         JRBeanCollectionDataSource acoesDataSource =
//             new JRBeanCollectionDataSource(raas.getAcoes());

//         params.put("acoesDataset", acoesDataSource);


//         InputStream reportStream = getClass()
//             .getResourceAsStream("/reports/raas.jrxml");

//         if (reportStream == null) {
//             throw new RuntimeException("Template RAAS não encontrado em /reports/raas.jrxml");
//         }

//         JasperReport jasperReport =
//             JasperCompileManager.compileReport(reportStream);

//         JasperPrint jasperPrint =
//             JasperFillManager.fillReport(
//                 jasperReport,
//                 params,
//                 new JREmptyDataSource()
//             );

//         return JasperExportManager.exportReportToPdf(jasperPrint);

//     } catch (Exception e) {
//         throw new RuntimeException("Erro ao gerar PDF do RAAS", e);
//     }
// }
    return null;
    }
}
