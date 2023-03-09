package com.noljanolja.server.consumer.config

import kotlinx.serialization.json.Json
import org.springframework.context.annotation.Configuration
import org.springframework.http.codec.*
import org.springframework.http.codec.json.KotlinSerializationJsonDecoder
import org.springframework.http.codec.json.KotlinSerializationJsonEncoder
import org.springframework.web.reactive.config.WebFluxConfigurer


@Configuration
class ServerConfigurer : WebFluxConfigurer {

    override fun configureHttpMessageCodecs(configurer: ServerCodecConfigurer) {
        configurer.useKotlinSerialization()
    }

}

/**
 * override default readers and writers
 */
internal fun CodecConfigurer.useKotlinSerialization() {
    val decoder = KotlinSerializationJsonDecoder(
        Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
            coerceInputValues = true
        }
    )
    val encoder = KotlinSerializationJsonEncoder(
        Json {
            encodeDefaults = true
        }
    )

    /**
     * Find all default readers and writers & filter out all readers and writers
     * depends on Jackson to add it later
     */
    val defaultReader = readers.filter {
        !((it is DecoderHttpMessageReader<*> && (it.decoder is KotlinSerializationJsonDecoder)) || (it is ServerSentEventHttpMessageReader))
    }
    val defaultWriter = writers.filter {
        !((it is EncoderHttpMessageWriter<*> && (it.encoder is KotlinSerializationJsonEncoder)) || (it is ServerSentEventHttpMessageWriter))
    }
    /**
     * Ignore register defaults reader and writer
     */
    registerDefaults(false)

    /**
     * Register default readers and writer that not depend on Jackson as customCodecs
     */
    (defaultReader + defaultWriter).forEach { customCodecs().registerWithDefaultConfig(it) }
    /**
     * Fore default readers and writers that depend on Jackson before to use Kotlin Serialization
     * then register it again
     */
    customCodecs().registerWithDefaultConfig(EncoderHttpMessageWriter(encoder))
    customCodecs().registerWithDefaultConfig(DecoderHttpMessageReader(decoder))
    customCodecs().registerWithDefaultConfig(ServerSentEventHttpMessageReader(decoder))
    customCodecs().registerWithDefaultConfig(ServerSentEventHttpMessageWriter(encoder))
}
