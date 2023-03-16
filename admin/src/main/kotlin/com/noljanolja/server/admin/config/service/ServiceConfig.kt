package com.noljanolja.server.admin.config.service

import com.noljanolja.server.common.config.YmlPropertySourceFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource

@ConfigurationProperties(prefix = "services")
data class ServiceConfig(val configs: List<Config>) {

    data class Config(
        val id: ServiceID,
        val baseUrl: String,
        val timeoutMillis: Long = 5000L,
        val extra: Map<String, String> = mutableMapOf(),
    ) {
        enum class ServiceID {
            CORE,
            AUTH,
        }
    }
}

@Configuration
@EnableConfigurationProperties(ServiceConfig::class)
@PropertySource(value = ["classpath:services.yml"], factory = YmlPropertySourceFactory::class)
class ServiceConfigLoader
