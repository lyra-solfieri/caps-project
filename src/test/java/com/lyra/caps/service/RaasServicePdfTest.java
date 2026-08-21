package com.lyra.caps.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.lyra.caps.model.Paciente;
import com.lyra.caps.model.Raas;
import com.lyra.caps.model.RaasAcao;
import com.lyra.caps.repository.AtendimentoRepository;
import com.lyra.caps.repository.PacienteRepository;
import com.lyra.caps.repository.RaasRepository;

class RaasServicePdfTest {

    private RaasRepository raasRepository;
    private PacienteRepository pacienteRepository;
    private AtendimentoRepository atendimentoRepository;
    private AuditoriaService auditoriaService;
    private UserService userService;
    private RaasService service;

    @BeforeEach
    void setUp() {
        raasRepository = mock(RaasRepository.class);
        pacienteRepository = mock(PacienteRepository.class);
        atendimentoRepository = mock(AtendimentoRepository.class);
        auditoriaService = mock(AuditoriaService.class);
        userService = mock(UserService.class);
        service = new RaasService(raasRepository, pacienteRepository,
                atendimentoRepository, auditoriaService, userService);
    }

    @Test
    void gerarPdfRetornaBytesValidosComTodosOsCampos() {
        Raas raas = raasCompleto();
        when(raasRepository.findById(30L)).thenReturn(Optional.of(raas));

        byte[] pdf = service.gerarPdf(30L);

        assertNotNull(pdf);
        assertTrue(pdf.length > 0);
        assertEquals("%PDF", new String(pdf, 0, 4, java.nio.charset.StandardCharsets.US_ASCII));
    }

    @Test
    void gerarPdfSemAcoesTambemGeraDocumento() {
        Raas raas = raasCompleto();
        raas.setAcoes(List.of());
        when(raasRepository.findById(30L)).thenReturn(Optional.of(raas));

        byte[] pdf = service.gerarPdf(30L);

        assertNotNull(pdf);
        assertTrue(pdf.length > 0);
    }

    @Test
    void gerarPdfInexistenteLancaEntityNotFound() {
        when(raasRepository.findById(99L)).thenReturn(Optional.empty());

        org.junit.jupiter.api.Assertions.assertThrows(
                jakarta.persistence.EntityNotFoundException.class,
                () -> service.gerarPdf(99L));
    }

    private Raas raasCompleto() {
        Paciente paciente = new Paciente();
        paciente.setId(10L);
        paciente.setNome("MARIA DA SILVA");

        Raas raas = new Raas();
        raas.setId(30L);
        raas.setNomeEstabelecimento("CAPS EXEMPLO");
        raas.setCnes("1234567");
        raas.setPaciente(paciente);
        raas.setNomePaciente("MARIA DA SILVA");
        raas.setCartaoSus("123456789012345");
        raas.setSexo("FEMININO");
        raas.setDataNascimento("1990-05-12");
        raas.setNomeMae("JOANA DA SILVA");
        raas.setDataAdmissao(LocalDate.of(2026, 8, 1));
        raas.setDataConclusao(LocalDate.of(2026, 8, 15));
        raas.setMesAtendimento("202608");
        raas.setNumeroAutorizacao(987654L);
        raas.setStatus(true);
        raas.setEnviadoSUS(false);
        raas.setDataGeracao(LocalDate.now());
        raas.setAcoes(List.of(acao(raas)));
        return raas;
    }

    private RaasAcao acao(Raas raas) {
        RaasAcao acao = new RaasAcao();
        acao.setCodigoAcao("0301010044");
        acao.setQuantidade(2);
        acao.setData(LocalDate.of(2026, 8, 10));
        acao.setCid10("F20");
        acao.setServico("CAPS III");
        acao.setClassificacao("SESSÃO");
        acao.setCboExecutor("251510");
        acao.setCartaoProfissional("123456789012345");
        acao.setCodigoEquipe("EQ001");
        acao.setCaps(true);
        acao.setTerritorio(false);
        acao.setRaas(raas);
        return acao;
    }
}
