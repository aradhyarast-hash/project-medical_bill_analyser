package com.analyzerbackend.backend.Config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI medicalBillAnalyserOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Medical Bill Analyser REST API")
                        .description("API for extracting medicines from bills/prescriptions, comparing active ingredients/salts, finding cheaper and Jan Aushadhi alternatives, and tagging options.")
                        .version("1.0.0")
                        .contact(new Contact().name("Medical Bill Analyser Team")));
    }
}
