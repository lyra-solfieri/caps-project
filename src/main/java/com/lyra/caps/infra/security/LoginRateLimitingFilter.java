package com.lyra.caps.infra.security;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

@Component
public class LoginRateLimitingFilter extends OncePerRequestFilter {

    private final Map<String, Deque<Instant>> attempts = new ConcurrentHashMap<>();

    private static final int MAX_ATTEMPTS = 5;
    private static final Duration WINDOW = Duration.ofMinutes(15);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        if (!isLoginRequest(request)) {
            chain.doFilter(request, response);
            return;
        }

        String ip = getClientIP(request);

        if (isBlocked(ip)) {
            response.setStatus(429);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\":\"Muitas tentativas de login. Tente novamente em 15 minutos.\"}");
            return;
        }

        StatusCapturingResponse captured = new StatusCapturingResponse(response);
        chain.doFilter(request, captured);

        if (captured.isFailure()) {
            recordAttempt(ip);
        } else {
            clearAttempts(ip);
        }
    }

    private boolean isLoginRequest(HttpServletRequest request) {
        return "/auth/login".equals(request.getRequestURI()) && "POST".equals(request.getMethod());
    }

    private boolean isBlocked(String ip) {
        Deque<Instant> deque = attempts.get(ip);
        if (deque == null) return false;
        cleanup(deque);
        if (deque.isEmpty()) {
            attempts.remove(ip);
            return false;
        }
        return deque.size() >= MAX_ATTEMPTS;
    }

    private static boolean isFailureStatus(int status) {
        return status >= 400;
    }

    private void recordAttempt(String ip) {
        attempts.computeIfAbsent(ip, k -> new ConcurrentLinkedDeque<>()).addLast(Instant.now());
    }

    private void clearAttempts(String ip) {
        attempts.remove(ip);
    }

    private static class StatusCapturingResponse extends HttpServletResponseWrapper {

        private int status = 200;

        StatusCapturingResponse(HttpServletResponse response) {
            super(response);
        }

        @Override
        public void setStatus(int status) {
            this.status = status;
            super.setStatus(status);
        }

        @Override
        public void sendError(int status, String message) throws IOException {
            this.status = status;
            super.sendError(status, message);
        }

        @Override
        public void sendError(int status) throws IOException {
            this.status = status;
            super.sendError(status);
        }

        boolean isFailure() {
            return isFailureStatus(status);
        }
    }

    private void cleanup(Deque<Instant> deque) {
        Instant cutoff = Instant.now().minus(WINDOW);
        while (!deque.isEmpty() && deque.peekFirst().isBefore(cutoff)) {
            deque.pollFirst();
        }
    }

    private String getClientIP(HttpServletRequest request) {
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

}
