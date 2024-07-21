package com.example.cooperativa.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class SwaggerConfigTest {

    @InjectMocks
    @SuppressWarnings("unused")
    private SwaggerConfig swaggerConfig;

    private AnnotationConfigApplicationContext context;

    @BeforeEach
    void setUp() {
        context = new AnnotationConfigApplicationContext();
        context.registerBean(SwaggerConfig.class, SwaggerConfig::new);
        context.refresh();
    }

    @Test
    void deveRetornarOsValoresDaDocumentacaoDoSwagger() {
        final OpenAPI openAPI = context.getBean(OpenAPI.class);
        assertNotNull(openAPI, "OpenAPI bean should not be null");

        final Info info = openAPI.getInfo();
        assertNotNull(info, "Info object should not be null");
        assertEquals("Cooperativa API", info.getTitle(), "Title should match");
        assertEquals("v0.0.1", info.getVersion(), "Version should match");
        assertEquals("API para gerenciar pautas e votações de uma cooperativa",
                info.getDescription(), "Description should match");

        final License license = info.getLicense();
        assertNotNull(license, "License object should not be null");
        assertEquals("Apache 2.0", license.getName(), "License name should match");
        assertEquals("http://springdoc.org", license.getUrl(), "License URL should match");
    }
}