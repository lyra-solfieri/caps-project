package com.lyra.caps.mapper;

import org.springframework.stereotype.Component;

import com.lyra.caps.dto.AnamneseDto;
import com.lyra.caps.model.Anamnese;

@Component
public class AnamneseMapper {

    public void updateEntityFromDto(AnamneseDto dto, Anamnese entity) {
        if (dto == null || entity == null) {
            return;
        }

        entity.setProfissionalResponsavel(dto.getProfissionalResponsavel());
        entity.setQueixaPrincipal(dto.getQueixaPrincipal());
        entity.setHistoriaDoencaAtual(dto.getHistoriaDoencaAtual());
        entity.setEncaminhamentos(dto.getEncaminhamentos());
        entity.setAntecedentesFamiliares(dto.getAntecedentesFamiliares());
        entity.setHabitosDeVida(dto.getHabitosDeVida());
        entity.setObservacoesGerais(dto.getObservacoesGerais());
        entity.setAlergias(dto.getAlergias());
        entity.setCirurgias(dto.getCirurgias());
        entity.setMedicamentosUsados(dto.getMedicamentosUsados());
        entity.setHistoricoDeInternacao(dto.getHistoricoDeInternacao());
        entity.setObservacoesGestacao(dto.getObservacoesGestacao());
        entity.setLocalNascimento(dto.getLocalNascimento());
        entity.setTipoParto(dto.getTipoParto());
        entity.setPosicaoNascimento(dto.getPosicaoNascimento());
        entity.setChorouAoNascer(dto.getChorouAoNascer());
        entity.setNasceuVermelho(dto.getNasceuVermelho());
        entity.setNasceuRoxo(dto.getNasceuRoxo());
        entity.setAnoxia(dto.getAnoxia());
        entity.setIctericia(dto.getIctericia());
        entity.setPrecisouOxigenio(dto.getPrecisouOxigenio());
        entity.setIncubadora(dto.getIncubadora());
        entity.setAltaHospitalar(dto.getAltaHospitalar());
        entity.setSorriu(dto.getSorriu());
        entity.setEquilibrouPescoco(dto.getEquilibrouPescoco());
        entity.setEngatinhou(dto.getEngatinhou());
        entity.setSentou(dto.getSentou());
        entity.setAndou(dto.getAndou());
        entity.setPrimeirasPalavras(dto.getPrimeirasPalavras());
        entity.setFalaCorreta(dto.getFalaCorreta());
        entity.setTrocouLetras(dto.getTrocouLetras());
        entity.setGaguejou(dto.getGaguejou());
        entity.setDenticao(dto.getDenticao());
        entity.setControleEsfincterAnalDiurno(dto.getControleEsfincterAnalDiurno());
        entity.setControleEsfincterVesicalDiurno(dto.getControleEsfincterVesicalDiurno());
        entity.setControleEsfincterNoturno(dto.getControleEsfincterNoturno());
        entity.setCuidadosPorQuem(dto.getCuidadosPorQuem());
        entity.setUsouChupeta(dto.getUsouChupeta());
        entity.setChupouDedo(dto.getChupouDedo());
        entity.setRoeUnhas(dto.getRoeUnhas());
        entity.setPuxaOrelha(dto.getPuxaOrelha());
        entity.setArrancaCabelos(dto.getArrancaCabelos());
        entity.setMordeLabios(dto.getMordeLabios());
        entity.setTiques(dto.getTiques());
        entity.setAtitudeDianteHabitos(dto.getAtitudeDianteHabitos());
        entity.setDormeBem(dto.getDormeBem());
        entity.setPulaQuandoDorme(dto.getPulaQuandoDorme());
        entity.setBabaNoite(dto.getBabaNoite());
        entity.setSudorese(dto.getSudorese());
        entity.setAcordaVariasVezes(dto.getAcordaVariasVezes());
        entity.setFalaDormindo(dto.getFalaDormindo());
        entity.setGritaDormindo(dto.getGritaDormindo());
        entity.setRangeDentes(dto.getRangeDentes());
        entity.setSonambulo(dto.getSonambulo());
        entity.setPesadelos(dto.getPesadelos());
        entity.setAmamentadoNoPeito(dto.getAmamentadoNoPeito());
        entity.setAtitudeDesmame(dto.getAtitudeDesmame());
        entity.setHabitosAlimentares(dto.getHabitosAlimentares());
        entity.setComportamentoEscola(dto.getComportamentoEscola());
        entity.setGostaEstudar(dto.getGostaEstudar());
        entity.setHistoricoEscolar(dto.getHistoricoEscolar());
        entity.setGostaEscola(dto.getGostaEscola());
        entity.setQueixasComportamento(dto.getQueixasComportamento());
        entity.setDificuldadeAprendizagem(dto.getDificuldadeAprendizagem());
        entity.setPrefereBrincar(dto.getPrefereBrincar());
        entity.setAtivoOuPassivo(dto.getAtivoOuPassivo());
        entity.setObservacoesComportamentoSocial(dto.getObservacoesComportamentoSocial());
        entity.setObservacoesEstrategicas(dto.getObservacoesEstrategicas());
        entity.setProjetoTerapeuticoSingular(dto.getProjetoTerapeuticoSingular());
    }
}
