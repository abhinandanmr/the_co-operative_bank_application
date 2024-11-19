package com.abhi.the_bank_app.utils;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("The Konandur Co-operative Bank APIs")
                        .description("Welcome to the Konandur Co-operative Bank API documentation. These APIs provide access to the core banking features of the system. You can perform operations such as creating accounts, managing transactions, and viewing account details.")
                        .version("2.1")
                        .contact(new Contact()
                                .name("Abhinandan M R")
                                .url("https://www.example.com") // Replace with the actual URL
                                .email("abhinandanmr@example.com")) // Replace with the actual email
                        .license(new License()
                                .name("Konandur Co-operative Bank License")
                                .url("https://www.example.com/license")) // Replace with the actual license URL
                );
    }
}
