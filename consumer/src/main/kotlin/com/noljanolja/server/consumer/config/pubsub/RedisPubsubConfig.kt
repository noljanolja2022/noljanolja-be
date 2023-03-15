package com.noljanolja.server.consumer.config.pubsub

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.noljanolja.server.consumer.model.Conversation
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.listener.ReactiveRedisMessageListenerContainer
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer


@Configuration
class RedisPubSubConfig {
    @Bean
    fun reactiveRedisTemplate(
        factory: ReactiveRedisConnectionFactory,
    ): ReactiveRedisTemplate<String, Conversation> {
        return buildRedisTemplate(factory)
    }

    @Bean
    fun container(
        factory: ReactiveRedisConnectionFactory,
    ): ReactiveRedisMessageListenerContainer {
        return ReactiveRedisMessageListenerContainer(factory)
    }

    private inline fun <reified V : Any> buildRedisTemplate(
        connectionFactory: ReactiveRedisConnectionFactory,
    ): ReactiveRedisTemplate<String, V> {
        val serializationContext = RedisSerializationContext.newSerializationContext<String, V>(StringRedisSerializer())
            .key(StringRedisSerializer())
            .value(Jackson2JsonRedisSerializer(
                jacksonObjectMapper().registerModule(JavaTimeModule()),
                V::class.java)
            )
            .build()

        return ReactiveRedisTemplate(connectionFactory, serializationContext)
    }
}