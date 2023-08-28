package com.noljanolja.server.consumer.config

import com.noljanolja.server.common.config.YmlPropertySourceFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource

@ConfigurationProperties(prefix = "config")
data class AppConfig(
    val baseUrl: String,
)

@Configuration
@EnableConfigurationProperties(AppConfig::class)
@PropertySource(value = ["classpath:app-config.yml"], factory = YmlPropertySourceFactory::class)
class AppConfigLoader
