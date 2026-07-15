package com.lyra.caps.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lyra.caps.model.Atendimento;

public interface AtendimentoRepository extends JpaRepository<Atendimento, Long> {

    long count();

    @Query("SELECT COUNT(a) FROM Atendimento a WHERE " +
           "(:inicio IS NULL OR a.dataAdmissao >= :inicio) AND " +
           "(:fim IS NULL OR a.dataAdmissao <= :fim)")
    long countByPeriodo(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);

    @Query("SELECT a.mesAtendimento, COUNT(a) FROM Atendimento a WHERE " +
           "(:inicio IS NULL OR a.dataAdmissao >= :inicio) AND " +
           "(:fim IS NULL OR a.dataAdmissao <= :fim) GROUP BY a.mesAtendimento ORDER BY a.mesAtendimento")
    List<Object[]> countGroupedByMes(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);

}
