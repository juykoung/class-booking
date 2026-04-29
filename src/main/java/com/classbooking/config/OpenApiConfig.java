package com.classbooking.config;

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
    public OpenAPI openAPI() {
        String memberIdHeader = "X-Member-Id";

        return new OpenAPI()
                .info(new Info()
                        .title("Class Booking API")
                        .description("Lecture, enrollment, and payment APIs for class booking.")
                        .version("v1"))
                .components(new Components()
                        .addSecuritySchemes(memberIdHeader, new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name(memberIdHeader)))
                .addSecurityItem(new SecurityRequirement().addList(memberIdHeader));
    }
}
