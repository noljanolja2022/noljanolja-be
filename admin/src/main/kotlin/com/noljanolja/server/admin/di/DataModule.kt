package com.noljanolja.server.admin.di

import com.noljanolja.server.admin.config.service.ServiceConfig
import io.netty.channel.ChannelOption
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import reactor.netty.resources.ConnectionProvider
import java.time.Duration
import java.util.concurrent.TimeUnit

@Configuration
class DataModule {
    companion object {
        private const val MAX_IN_MEMORY_SIZE = 2_097_152
        private const val MAX_CONNECTIONS = 24
        private const val MAX_CONNECTION_LIFE_TIME_SECONDS = 300L
        private const val MAX_CONNECTION_IDLE_TIME_SECONDS = 15L
    }

    @Bean
    fun authWebClient(
        webClientBuilder: WebClient.Builder,
        serviceConfig: ServiceConfig,
    ): WebClient {
        val config = serviceConfig.configs.first { it.id == ServiceConfig.Config.ServiceID.AUTH }
        return buildWebClient(webClientBuilder, config)
    }

    @Bean
    fun coreWebClient(
        webClientBuilder: WebClient.Builder,
        serviceConfig: ServiceConfig,
    ): WebClient {
        val config = serviceConfig.configs.first { it.id == ServiceConfig.Config.ServiceID.CORE }
        return buildWebClient(webClientBuilder, config)
    }

    private fun buildWebClient(
        webClientBuilder: WebClient.Builder,
        config: ServiceConfig.Config,
        filters: List<ExchangeFilterFunction> = listOf(),
    ): WebClient {
        val connectionProvider = ConnectionProvider.builder(config.id.name)
            .maxConnections(MAX_CONNECTIONS)
            .maxLifeTime(Duration.ofSeconds(MAX_CONNECTION_LIFE_TIME_SECONDS))
            .maxIdleTime(Duration.ofSeconds(MAX_CONNECTION_IDLE_TIME_SECONDS))
            .build()

        val httpClient = HttpClient.create(connectionProvider)
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, config.timeoutMillis.toInt())
            .doOnConnected { connection ->
                connection.addHandlerLast(
                    ReadTimeoutHandler(config.timeoutMillis, TimeUnit.MILLISECONDS)
                )
                connection.addHandlerLast(
                    WriteTimeoutHandler(config.timeoutMillis, TimeUnit.MILLISECONDS)
                )
            }
        return webClientBuilder
            .baseUrl(config.baseUrl)
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .exchangeStrategies(
                ExchangeStrategies
                    .builder()
                    .codecs { it.defaultCodecs().maxInMemorySize(MAX_IN_MEMORY_SIZE) }
                    .build()
            )
            .filters { it.addAll(filters) }
            .build()
    }
}