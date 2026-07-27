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

@Component
public class LoginRateLimitingFilter extends OncePerRequestFilter {

    private final Map<String, Deque<Instant>> attempts = new ConcurrentHashMap<>();

    private static final int MAX_ATTEMPTS = 5;
    private static final Duration WINDOW = Duration.ofMinutes(15);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        if (isLoginRequest(request)) {
            String ip = getClientIP(request);
            if (isBlocked(ip)) {
                response.setStatus(429);
                response.setContentType("application/json");
                response.getWriter().write("{\"message\":\"Muitas tentativas de login. Tente novamente em 15 minutos.\"}");
                return;
            }
            recordAttempt(ip);
        }

        chain.doFilter(request, response);
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

    private void recordAttempt(String ip) {
        attempts.computeIfAbsent(ip, k -> new ConcurrentLinkedDeque<>()).addLast(Instant.now());
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
