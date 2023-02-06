package com.noljanolja.server.consumer.config.cache

import com.noljanolja.server.consumer.config.cache.serializer.Kotlin2JsonRedisSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.Json.Default.serializersModule
import kotlinx.serialization.serializer
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer


@ConfigurationProperties(prefix = "cache")
@ConstructorBinding
data class CacheProperties(
    val enable: Boolean = true,
    val properties: List<CacheProperty>,
) {
    data class CacheProperty(
        val id: CacheId,
        val enable: Boolean = true,
        val timeToLiveSeconds: Long = 300,
    )

    enum class CacheId {
    }
}

@Configuration
class RedisConfig {
    private inline fun <reified V : Any> buildRedisTemplate(
        connectionFactory: ReactiveRedisConnectionFactory,
    ): ReactiveRedisTemplate<String, V> {
        val serializationContext = RedisSerializationContext.newSerializationContext<String, V>(StringRedisSerializer())
            .hashKey(StringRedisSerializer())
            .hashValue(
                Kotlin2JsonRedisSerializer(
                    Json {
                        ignoreUnknownKeys = true
                        encodeDefaults = true
                        coerceInputValues = true
                    },
                    serializersModule.serializer<V>(),
                )
            )
            .build()

        return ReactiveRedisTemplate(connectionFactory, serializationContext)
    }
}