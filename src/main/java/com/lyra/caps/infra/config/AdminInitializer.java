package com.lyra.caps.infra.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.lyra.caps.model.User;
import com.lyra.caps.model.enums.Role;
import com.lyra.caps.repository.UserRepository;

@Component
public class AdminInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    public AdminInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.findByRole(Role.ADMIN).isEmpty()) {
            User user = new User(adminEmail, passwordEncoder.encode(adminPassword), Role.ADMIN);
            user.setNome("Bruno Lyra");
            user.setBloqueado(false);
            user.setProfissaoUsuario("Administrador do Sistema");
            userRepository.save(user);
        }
    }

}
