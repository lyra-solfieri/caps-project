package com.lyra.caps.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.lyra.caps.dto.AuditoriaLogResponse;
import com.lyra.caps.model.AuditoriaLog;
import com.lyra.caps.model.User;
import com.lyra.caps.model.enums.AcaoAuditoria;
import com.lyra.caps.repository.AuditoriaLogRepository;
import com.lyra.caps.util.LogPersonalizado;

import jakarta.persistence.criteria.Predicate;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class AuditoriaService implements IAuditoriaService {

    private static final int MAX_PAGE_SIZE = 100;
    private static final int MAX_USUARIO_LENGTH = 100;

    private final AuditoriaLogRepository auditoriaLogRepository;
    private final LogPersonalizado log;

    public AuditoriaService(AuditoriaLogRepository auditoriaLogRepository) {
        this.auditoriaLogRepository = auditoriaLogRepository;
        this.log = LogPersonalizado.getLogger(this.getClass());
    }

    @Override
    public void registrar(String usuario, AcaoAuditoria acao, String entidade, Long entidadeId, String detalhes, String ip) {
        AuditoriaLog auditLog = AuditoriaLog.builder()
                .usuario(usuario != null ? usuario : getCurrentUsername())
                .acao(acao)
                .entidade(entidade)
                .entidadeId(entidadeId)
                .detalhes(detalhes)
                .ip(ip != null ? ip : getClientIp())
                .dataHora(LocalDateTime.now())
                .build();
        auditoriaLogRepository.save(auditLog);
        log.info("Auditoria: " + auditLog.getUsuario() + " - " + acao + " em " + entidade
                + (entidadeId != null ? "(" + entidadeId + ")" : ""));
    }

    public String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            Object principal = auth.getPrincipal();
            if (principal instanceof User user) {
                return user.getEmail();
            }
            return principal.toString();
        }
        return "desconhecido";
    }

    public String getClientIp() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String ip = request.getHeader("X-Forwarded-For");
            if (ip == null || ip.isBlank()) {
                ip = request.getHeader("X-Real-IP");
            }
            if (ip == null || ip.isBlank()) {
                ip = request.getRemoteAddr();
            }
            if (ip != null && ip.contains(",")) {
                ip = ip.split(",")[0].trim();
            }
            return ip;
        }
        return "desconhecido";
    }

    @Override
    public Page<AuditoriaLogResponse> listarLogs(int page, int size, AcaoAuditoria acao, String entidade, String usuario, LocalDateTime dataInicio, LocalDateTime dataFim) {
        LocalDateTime inicio = LocalDateTime.now();

        int pageSafe = Math.max(page, 0);
        int sizeSafe = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        String usuarioFiltro = usuario;
        if (usuarioFiltro != null && usuarioFiltro.length() > MAX_USUARIO_LENGTH) {
            log.warn("Filtro de usuario truncado de " + usuarioFiltro.length() + " para " + MAX_USUARIO_LENGTH + " caracteres");
            usuarioFiltro = usuario.substring(0, MAX_USUARIO_LENGTH);
        }

        log.info("listarLogs -> page=" + pageSafe + ", size=" + sizeSafe
                + ", acao=" + acao + ", entidade=" + entidade
                + ", usuario=" + usuario + ", dataInicio=" + dataInicio
                + ", dataFim=" + dataFim);
                
        String usuarioFinal = usuarioFiltro;

        Specification<AuditoriaLog> spec = (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            if (acao != null) {
                predicate = cb.and(predicate, cb.equal(root.get("acao"), acao));
            }
            if (entidade != null && !entidade.isBlank()) {
                predicate = cb.and(predicate, cb.equal(root.get("entidade"), entidade));
            }
            if (usuarioFinal != null && !usuarioFinal.isBlank()) {
                predicate = cb.and(predicate, cb.like(cb.lower(root.get("usuario")), "%" + usuarioFinal.toLowerCase() + "%"));
            }
            if (dataInicio != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("dataHora"), dataInicio));
            }
            if (dataFim != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("dataHora"), dataFim));
            }

            return predicate;
        };

        PageRequest pageRequest = PageRequest.of(pageSafe, sizeSafe, Sort.by(Sort.Direction.DESC, "dataHora"));

        try {
            Page<AuditoriaLogResponse> result = auditoriaLogRepository.findAll(spec, pageRequest)
                    .map(AuditoriaLogResponse::fromEntity);

            Duration elapsed = Duration.between(inicio, LocalDateTime.now());
            log.info("listarLogs -> " + result.getTotalElements() + " resultados encontrados em " + elapsed.toMillis() + "ms");

            if (result.isEmpty() && (acao != null || entidade != null || usuario != null || dataInicio != null || dataFim != null)) {
                log.warn("listarLogs -> consulta com filtros nao retornou resultados");
            }

            return result;
        } catch (Exception e) {
            log.error("Erro ao listar logs de auditoria", e);
            throw e;
        }
    }

    @Override
    public List<String> listarUsuarios() {
        LocalDateTime inicio = LocalDateTime.now();
        log.info("listarUsuarios");

        try {
            List<String> usuarios = auditoriaLogRepository.findDistinctUsuarios();
            Duration elapsed = Duration.between(inicio, LocalDateTime.now());
            log.info("listarUsuarios -> " + usuarios.size() + " usuarios encontrados em " + elapsed.toMillis() + "ms");
            return usuarios;
        } catch (Exception e) {
            log.error("Erro ao listar usuarios da auditoria", e);
            throw e;
        }
    }

    @Override
    public List<String> listarEntidades() {
        LocalDateTime inicio = LocalDateTime.now();
        log.info("listarEntidades");

        try {
            List<String> entidades = auditoriaLogRepository.findDistinctEntidades();
            Duration elapsed = Duration.between(inicio, LocalDateTime.now());
            log.info("listarEntidades -> " + entidades.size() + " entidades encontradas em " + elapsed.toMillis() + "ms");
            return entidades;
        } catch (Exception e) {
            log.error("Erro ao listar entidades da auditoria", e);
            throw e;
        }
    }
}
