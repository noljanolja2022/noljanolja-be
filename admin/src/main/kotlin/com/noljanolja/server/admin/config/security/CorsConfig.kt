package com.noljanolja.server.admin.config.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsWebFilter
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource


@Configuration
class CorsConfig {
    @Bean
    fun corsWebFilter(): CorsWebFilter {
        val corsConfiguration = CorsConfiguration().apply {
            allowedOriginPatterns = listOf(
                "http://localhost:*",
            )
            maxAge = 300L
            allowedMethods = listOf("*")
            allowedHeaders = listOf("*")
        }
        val source = UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", corsConfiguration)
        }
        return CorsWebFilter(source)
    }
}
