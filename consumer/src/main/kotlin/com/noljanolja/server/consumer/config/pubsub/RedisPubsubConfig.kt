package com.noljanolja.server.consumer.config.pubsub

import com.noljanolja.server.consumer.model.Conversation
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.listener.ReactiveRedisMessageListenerContainer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer


@Configuration
class RedisPubSubConfig {
    @Bean
    fun reactiveRedisTemplate(factory: ReactiveRedisConnectionFactory): ReactiveRedisTemplate<String, Conversation> {
        return buildRedisTemplate(factory)
    }

    @Bean
    fun container(factory: ReactiveRedisConnectionFactory): ReactiveRedisMessageListenerContainer {
        return ReactiveRedisMessageListenerContainer(factory)
    }

    @OptIn(ExperimentalSerializationApi::class)
    private inline fun <reified V : Any> buildRedisTemplate(
        connectionFactory: ReactiveRedisConnectionFactory,
    ): ReactiveRedisTemplate<String, V> {
        val serializationContext = RedisSerializationContext.newSerializationContext<String, V>(StringRedisSerializer())
            .key(StringRedisSerializer())
            .value(
                Kotlin2JsonRedisSerializer(
                    Json {
                        ignoreUnknownKeys = true
                        encodeDefaults = true
                        coerceInputValues = true
                    },
                    Json.serializersModule.serializer<V>(),
                )
            )
            .build()

        return ReactiveRedisTemplate(connectionFactory, serializationContext)
    }
}