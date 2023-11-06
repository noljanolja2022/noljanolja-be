package com.nolgobuljia.server.giftbiz

import com.noljanolja.server.common.config.YmlPropertySourceFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource

@ConfigurationProperties(prefix = "gift-biz-services")
data class GiftBizServiceConfig(val configs: List<Config>) {

    data class Config(
        val id: ServiceID,
        val baseUrl: String,
        val timeoutMillis: Long = 5000L,
        val extra: Map<String, String> = mutableMapOf(),
    ) {
        enum class ServiceID {
            GIFTBIZ
        }
    }
}

@Configuration
@EnableConfigurationProperties(GiftBizServiceConfig::class)
@PropertySource(value = ["classpath:gift-biz-services.yml"], factory = YmlPropertySourceFactory::class)
class GiftBizServiceConfigLoader