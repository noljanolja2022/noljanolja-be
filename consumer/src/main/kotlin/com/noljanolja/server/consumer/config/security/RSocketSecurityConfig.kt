package com.noljanolja.server.consumer.config.security

import com.noljanolja.server.consumer.adapter.auth.AuthApi
import kotlinx.coroutines.reactor.mono
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.rsocket.EnableRSocketSecurity
import org.springframework.security.config.annotation.rsocket.RSocketSecurity
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken
import org.springframework.security.rsocket.core.PayloadSocketAcceptorInterceptor
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Configuration
@EnableRSocketSecurity
@EnableReactiveMethodSecurity
class RSocketSecurityConfig {
    @Bean
    fun payloadSocketAcceptorInterceptor(
        security: RSocketSecurity,
        rsocketBearerTokenReactiveAuthenticationManager: RsocketBearerTokenReactiveAuthenticationManager,
    ): PayloadSocketAcceptorInterceptor {
        security.authorizePayload { authorize: RSocketSecurity.AuthorizePayloadsSpec ->
            authorize
                .anyExchange().permitAll()
                .setup().permitAll()
                .anyRequest().authenticated()
        }
            .jwt { jwtSpec ->
                try {
                    jwtSpec.authenticationManager(rsocketBearerTokenReactiveAuthenticationManager)
                } catch (e: Exception) {
                    throw RuntimeException(e)
                }
            }
        return security.build()
    }
}

@Component
class RsocketBearerTokenReactiveAuthenticationManager(
    private val jwtCustomVerifier: JWTCustomVerifier,
) : ReactiveAuthenticationManager {
    override fun authenticate(authentication: Authentication): Mono<Authentication> {
        return jwtCustomVerifier.check((authentication as BearerTokenAuthenticationToken).token)
    }
}

@Component
class JWTCustomVerifier(
    private val authApi: AuthApi,
) {
    fun check(token: String): Mono<Authentication> = mono {
        val authUser = authApi.verifyToken(token)
        UsernamePasswordAuthenticationToken(authUser.id, null, listOf())
    }
}