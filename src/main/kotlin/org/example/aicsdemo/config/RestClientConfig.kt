package org.example.aicsdemo.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient

@Configuration
class RestClientConfig {
    @Bean
    fun restClient(): RestClient =
        RestClient
            .builder()
            .requestFactory(
                org.springframework.http.client
                    .JdkClientHttpRequestFactory(),
            ).defaultHeader("User-Agent", "AI-CS-Demo/1.0")
            .defaultHeader("Accept", "application/json")
            .build()
}
