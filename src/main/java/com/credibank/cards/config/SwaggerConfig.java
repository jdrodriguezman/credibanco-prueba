package com.credibank.cards.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.time.LocalTime;
import java.util.Collections;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(Boolean.FALSE)
                .directModelSubstitute(LocalTime.class, String.class)
                .select().apis(RequestHandlerSelectors.basePackage("com.credibanco.card"))
                .paths(PathSelectors.any()).build().apiInfo(new ApiInfo(
                        "CrediBanco Prueba",
                        "Credibank API",
                        "1.0",
                        "https://www.credibanco.com",
                        new Contact("Swagger/dev", "https://www.credibanco.com", ""), "", "",
                        Collections.emptyList()
                ));
    }
}

