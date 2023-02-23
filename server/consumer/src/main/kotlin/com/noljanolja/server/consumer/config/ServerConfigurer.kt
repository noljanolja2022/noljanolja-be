package com.noljanolja.server.consumer.config

import com.noljanolja.server.common.config.useKotlinSerialization
import org.springframework.context.annotation.Configuration
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.web.reactive.config.WebFluxConfigurer

@Configuration
class ServerConfigurer : WebFluxConfigurer {

    override fun configureHttpMessageCodecs(configurer: ServerCodecConfigurer) {
        configurer.useKotlinSerialization()
    }
}