package com.noljanolja.server.consumer.config.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain

@Configuration
@EnableWebFluxSecurity
class SecurityConfig {
    @Bean
    fun filterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        http.csrf().disable()
        return http.build()
    }
}