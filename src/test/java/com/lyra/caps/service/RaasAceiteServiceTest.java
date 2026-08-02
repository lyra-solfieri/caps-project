package com.lyra.caps.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.lyra.caps.dto.RaasAceitarDTO;
import com.lyra.caps.dto.RaasAcaoDto;
import com.lyra.caps.model.Atendimento;
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

class RaasAceiteServiceTest {

    private OcrProcessamentoRepository processamentoRepository;
    private PacienteRepository pacienteRepository;
    private AtendimentoRepository atendimentoRepository;
    private RaasRepository raasRepository;
    private RaasAceiteService service;

    @BeforeEach
    void setUp() {
        processamentoRepository = mock(OcrProcessamentoRepository.class);
        pacienteRepository = mock(PacienteRepository.class);
        atendimentoRepository = mock(AtendimentoRepository.class);
        raasRepository = mock(RaasRepository.class);
        service = new RaasAceiteService(processamentoRepository, pacienteRepository,
                atendimentoRepository, raasRepository);
    }

    @Test
    void aceitaCriandoPacienteAtendimentoERaasComStatusVerdadeiro() {
        RaasAceitarDTO dto = aceitarDto();
        OcrProcessamento processamento = processamento("abc-123", OcrStatus.CONCLUIDO);
        when(processamentoRepository.findByCorrelationId("abc-123"))
                .thenReturn(Optional.of(processamento));

        when(pacienteRepository.findByNumeroCartaoSUS(any())).thenReturn(Optional.empty());
        when(pacienteRepository.save(any(Paciente.class))).thenAnswer(inv -> {
            Paciente paciente = inv.getArgument(0);
            paciente.setId(10L);
            return paciente;
        });
        when(atendimentoRepository.save(any(Atendimento.class))).thenAnswer(inv -> {
            Atendimento atendimento = inv.getArgument(0);
            atendimento.setId(20L);
            return atendimento;
        });
        when(raasRepository.save(any(Raas.class))).thenAnswer(inv -> {
            Raas raas = inv.getArgument(0);
            raas.setId(30L);
            return raas;
        });

        service.aceitar(dto);

        ArgumentCaptor<Paciente> pacienteCaptor = ArgumentCaptor.forClass(Paciente.class);
        verify(pacienteRepository).save(pacienteCaptor.capture());
        Paciente paciente = pacienteCaptor.getValue();
        assertEquals("MARIA DA SILVA", paciente.getNome());
        assertEquals("123456789012345", paciente.getNumeroCartaoSUS());
        assertEquals(Genero.FEMININO, paciente.getGenero());
        assertEquals(LocalDate.of(1990, 3, 15), paciente.getDataNascimento());
        assertEquals(Raca.PARDA, paciente.getRaca());
        assertEquals("ANA DA SILVA", paciente.getNomeMae());
        assertEquals("000123", paciente.getNumeroProntuario());
        assertEquals("SÃO PAULO", paciente.getEndereco().getCidade());
        assertEquals("SP", paciente.getEndereco().getEstado());
        assertEquals("01001-000", paciente.getEndereco().getCep());

        ArgumentCaptor<Atendimento> atendimentoCaptor = ArgumentCaptor.forClass(Atendimento.class);
        verify(atendimentoRepository).save(atendimentoCaptor.capture());
        Atendimento atendimento = atendimentoCaptor.getValue();
        assertEquals(LocalDate.of(2024, 1, 1), atendimento.getDataAdmissao());
        assertEquals(LocalDate.of(2024, 1, 31), atendimento.getDataConclusao());
        assertEquals("01/2024", atendimento.getMesAtendimento());
        assertTrue(atendimento.isOrigemUrgencia());
        assertEquals("F32", atendimento.getCidPrincipal());
        assertEquals("S", atendimento.getEsf());
        assertTrue(atendimento.isAlta());

        ArgumentCaptor<Raas> raasCaptor = ArgumentCaptor.forClass(Raas.class);
        verify(raasRepository).save(raasCaptor.capture());
        Raas raas = raasCaptor.getValue();
        assertTrue(raas.isStatus());
        assertFalse(raas.isEnviadoSUS());
        assertEquals("MARIA DA SILVA", raas.getNomePaciente());
        assertEquals("123456789012345", raas.getCartaoSus());
        assertEquals("FEMININO", raas.getSexo());
        assertEquals(1, raas.getAcoes().size());
        RaasAcao acao = raas.getAcoes().get(0);
        assertEquals("1.01.01", acao.getCodigoAcao());
        assertEquals(2, acao.getQuantidade());
        assertEquals(LocalDate.of(2024, 1, 10), acao.getData());
        assertEquals("F32", acao.getCid10());
        assertEquals("9999", acao.getCodigoEquipe());

        assertEquals(10L, processamento.getPacienteCriadoId());
        assertEquals(20L, processamento.getAtendimentoCriadoId());
        assertEquals(30L, processamento.getRaasCriadoId());
    }

    @Test
    void rejeitaQuandoProcessamentoNaoEstaPronto() {
        OcrProcessamento processamento = processamento("falha", OcrStatus.FALHA);
        when(processamentoRepository.findByCorrelationId("falha"))
                .thenReturn(Optional.of(processamento));

        RaasAceitarDTO dto = aceitarDto();
        dto.setCorrelationId("falha");

        assertThrows(IllegalStateException.class, () -> service.aceitar(dto));
        verify(pacienteRepository, never()).save(any());
        verify(atendimentoRepository, never()).save(any());
        verify(raasRepository, never()).save(any());
    }

    @Test
    void rejeitaAceiteDuplicado() {
        OcrProcessamento processamento = processamento("dup", OcrStatus.CONCLUIDO);
        processamento.setRaasCriadoId(99L);
        when(processamentoRepository.findByCorrelationId("dup"))
                .thenReturn(Optional.of(processamento));

        RaasAceitarDTO dto = aceitarDto();
        dto.setCorrelationId("dup");

        assertThrows(IllegalStateException.class, () -> service.aceitar(dto));
        verify(raasRepository, never()).save(any());
    }

    @Test
    void usaPacienteExistentePorCns() {
        RaasAceitarDTO dto = aceitarDto();
        dto.setCorrelationId("cns");
        dto.setCartaoSus("111111111111111");

        OcrProcessamento processamento = processamento("cns", OcrStatus.CONCLUIDO);
        when(processamentoRepository.findByCorrelationId("cns"))
                .thenReturn(Optional.of(processamento));

        Paciente existente = new Paciente();
        existente.setId(50L);
        existente.setNome("PACIENTE EXISTENTE");
        existente.setNumeroCartaoSUS("111111111111111");
        when(pacienteRepository.findByNumeroCartaoSUS("111111111111111"))
                .thenReturn(Optional.of(existente));

        when(atendimentoRepository.save(any(Atendimento.class))).thenAnswer(inv -> inv.getArgument(0));
        when(raasRepository.save(any(Raas.class))).thenAnswer(inv -> inv.getArgument(0));

        service.aceitar(dto);

        ArgumentCaptor<Raas> raasCaptor = ArgumentCaptor.forClass(Raas.class);
        verify(raasRepository).save(raasCaptor.capture());
        assertEquals(50L, raasCaptor.getValue().getPaciente().getId());
        verify(pacienteRepository, never()).save(any());
    }

    private OcrProcessamento processamento(String correlationId, OcrStatus status) {
        OcrProcessamento processamento = new OcrProcessamento();
        processamento.setId(1L);
        processamento.setCorrelationId(correlationId);
        processamento.setStatus(status);
        return processamento;
    }

    private RaasAceitarDTO aceitarDto() {
        RaasAceitarDTO dto = new RaasAceitarDTO();
        dto.setCorrelationId("abc-123");
        dto.setNomeEstabelecimento("CAPS CENTRO");
        dto.setCnes("1234567");

        dto.setNumeroProntuario("000123");
        dto.setNomePaciente("MARIA DA SILVA");
        dto.setCartaoSus("123456789012345");
        dto.setSexo("F");
        dto.setDataNascimento(LocalDate.of(1990, 3, 15));
        dto.setNomeMae("ANA DA SILVA");
        dto.setNacionalidade("BRASILEIRA");
        dto.setRacaCor("PARDA");
        dto.setMunicipio("SÃO PAULO");
        dto.setUf("SP");
        dto.setCodigoIbge("3550308");
        dto.setCep("01001-000");
        dto.setEndereco("RUA DAS FLORES, 100");
        dto.setTelefoneCelular("11999999999");

        dto.setDataAdmissao(LocalDate.of(2024, 1, 1));
        dto.setMesAtendimento("01/2024");
        dto.setOrigemUrgencia(true);
        dto.setCidPrincipal("F32");
        dto.setDescricaoCidPrincipal("EPISODIO DEPRESSIVO");
        dto.setEsf("S");
        dto.setCnesAtendimento("1234567");
        dto.setAlta(true);
        dto.setDataConclusao(LocalDate.of(2024, 1, 31));

        RaasAcaoDto acao = new RaasAcaoDto();
        acao.setCodigoAcao("1.01.01");
        acao.setQuantidade(2);
        acao.setData(LocalDate.of(2024, 1, 10));
        acao.setCid10("F32");
        acao.setServico("Acolhimento");
        acao.setClassificacao("Avaliação");
        acao.setCboExecutor("251510");
        acao.setCartaoProfissional("123456789012345");
        acao.setCodigoEquipe("9999");
        dto.setAcoes(List.of(acao));

        return dto;
    }
}
