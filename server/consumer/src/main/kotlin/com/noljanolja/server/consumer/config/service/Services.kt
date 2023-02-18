package com.noljanolja.server.consumer.config.service

import com.noljanolja.server.common.config.YmlPropertySourceFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource

@Configuration
@EnableConfigurationProperties(Services::class)
@PropertySource(value = ["classpath:services.yml"], factory = YmlPropertySourceFactory::class)
class ServicesLoader

@ConstructorBinding
@ConfigurationProperties(prefix = "services")
data class Services(val properties: List<ServiceProperty>) {

    data class ServiceProperty(
        val id: ServiceID,
        val baseUrl: String,
        val timeoutMillis: Long = 5000L,
        val configs: Map<String, String> = mutableMapOf(),
    ) {
        enum class ServiceID {
            CORE,
            AUTH,
        }
    }
}