package com.persons.finder.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.servers.Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Configuration for OpenAPI/Swagger documentation
 */
@Configuration
class OpenApiConfig {

    @Bean
    fun openAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("Persons Finder API")
                    .description("API for finding people around a location")
                    .version("v1.0.0")
                    .contact(
                        Contact()
                            .name("Persons Finder Team")
                            .email("support@personsfinder.com")
                    )
                    .license(
                        License()
                            .name("Apache 2.0")
                            .url("https://www.apache.org/licenses/LICENSE-2.0")
                    )
            )
            .addServersItem(
                Server()
                    .url("/")
                    .description("Default Server URL")
            )
    }
}