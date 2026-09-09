package com.lyra.caps.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lyra.caps.dto.AuthenticationDto;
import com.lyra.caps.dto.LoginResponseDto;
import com.lyra.caps.dto.RegisterDto;
import com.lyra.caps.infra.security.TokenService;
import com.lyra.caps.model.User;
import com.lyra.caps.model.enums.AcaoAuditoria;
import com.lyra.caps.model.enums.Role;
import com.lyra.caps.repository.UserRepository;
import com.lyra.caps.service.AuditoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    private final TokenService tokenService;

    private final AuditoriaService auditoriaService;

    private final PasswordEncoder passwordEncoder;

    AuthenticationController(AuthenticationManager authenticationManager, UserRepository userRepository, TokenService tokenService, AuditoriaService auditoriaService, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.tokenService = tokenService;
        this.auditoriaService = auditoriaService;
        this.passwordEncoder = passwordEncoder;
    }
    
    @PostMapping("/login")
    @Operation(summary = "Autenticar usuário", description = "Realiza o login e retorna um token JWT para acesso aos demais endpoints.")
    @ApiResponse(responseCode = "200", description = "Login realizado com sucesso, retorna o token JWT")
    @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    @ApiResponse(responseCode = "429", description = "Muitas tentativas de login. Tente novamente em 15 minutos")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody AuthenticationDto authentication, HttpServletRequest request) {

        var userEmailPassword = new UsernamePasswordAuthenticationToken(authentication.email(), authentication.password());
        var auth = this.authenticationManager.authenticate(userEmailPassword);

        var token = tokenService.generateToken((User) auth.getPrincipal());

        String ip = getClientIp(request);
        auditoriaService.registrar(authentication.email(), AcaoAuditoria.LOGIN, "User", null, "Login realizado", ip);

        return ResponseEntity.ok(new LoginResponseDto(token));
    }

    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Registrar usuário", description = "Cria um novo usuário no sistema. Requer perfil ADMIN.")
    @ApiResponse(responseCode = "200", description = "Usuário registrado com sucesso")
    @ApiResponse(responseCode = "400", description = "Usuário já existente")
    @ApiResponse(responseCode = "403", description = "Acesso negado")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterDto register, HttpServletRequest request) {
        if(this.userRepository.findByEmail(register.email()) != null) {
            return ResponseEntity.badRequest().build();
        }

        Role role = register.role() != null ? register.role() : Role.USER;
        String encryptedPassword = passwordEncoder.encode(register.password());
        User user = new User(register.email(), encryptedPassword, role);
        user.setNome(register.nome());
        user.setProfissaoUsuario(register.profissao());
        this.userRepository.save(user);

        String ip = getClientIp(request);
        auditoriaService.registrar(register.email(), AcaoAuditoria.CRIACAO, "User", user.getId(), "Usuario registrado: " + register.email(), ip);

        return ResponseEntity.ok().build();
    }

    private String getClientIp(HttpServletRequest request) {
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
