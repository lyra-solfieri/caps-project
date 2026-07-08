package com.lyra.caps.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.lyra.caps.model.AuditoriaLog;

public interface AuditoriaLogRepository extends JpaRepository<AuditoriaLog, Long>, JpaSpecificationExecutor<AuditoriaLog> {

    @Query("SELECT DISTINCT a.usuario FROM AuditoriaLog a")
    java.util.List<String> findDistinctUsuarios();

    @Query("SELECT DISTINCT a.entidade FROM AuditoriaLog a")
    java.util.List<String> findDistinctEntidades();
}
