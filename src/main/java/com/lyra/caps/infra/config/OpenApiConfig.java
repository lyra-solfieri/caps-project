package com.lyra.caps.infra.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI capsOpenAPI() {
        final String securitySchemeName = "bearerAuth"; 

        return new OpenAPI()
                .info(new Info()
                        .title("CAPS API")
                        .version("1.0")
                        .description("API do sistema CAPS para gestão de pacientes, "
                                + "agendamentos, anamnese, evolução, RAAS, "
                                + "relatórios e auditoria."))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components().addSecuritySchemes(securitySchemeName,
                        new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Informe o token JWT obtido no endpoint /auth/login "
                                        + "como 'Bearer <token>'.")));
    }
}