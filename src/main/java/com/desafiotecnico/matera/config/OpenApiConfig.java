package com.desafiotecnico.matera.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Bank API - Desafio Técnico Matera",
                version = "1.0.0",
                description = "API RESTful para lançamentos bancários (débito/crédito), com controle de concorrência e histórico de transações.",
                contact = @Contact(
                        name = "Yago Martins",
                        email = "yagolopesmartins777@gmail.com"
                ),
                license = @License(
                        name = "Uso exclusivo para avaliação técnica"
                )
        )
)
public class OpenApiConfig {
}
