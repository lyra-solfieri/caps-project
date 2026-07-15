package com.lyra.caps.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lyra.caps.model.Agendamento;

public interface AgendamentoRepository  extends JpaRepository<Agendamento, Long> {

    long count();

    @Query("SELECT a.statusAgendamento, COUNT(a) FROM Agendamento a WHERE " +
           "(:inicio IS NULL OR a.dataHoraAtendimento >= :inicio) AND " +
           "(:fim IS NULL OR a.dataHoraAtendimento <= :fim) GROUP BY a.statusAgendamento")
    List<Object[]> countByStatusAndPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

}
