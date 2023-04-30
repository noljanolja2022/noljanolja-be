package com.noljanolja.server.consumer.config.pubsub

import com.fasterxml.jackson.databind.ObjectMapper
import com.noljanolja.server.consumer.model.Conversation
import com.noljanolja.server.consumer.model.VideoProgress
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.listener.ReactiveRedisMessageListenerContainer
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer


@Configuration
class RedisPubSubConfig(
    private val objectMapper: ObjectMapper,
) {
    @Bean
    fun reactiveRedisTemplate(
        factory: ReactiveRedisConnectionFactory,
    ): ReactiveRedisTemplate<String, Conversation> {
        return buildRedisTemplate(factory)
    }

    @Bean
    fun videoRedisTemplate(
        factory: ReactiveRedisConnectionFactory,
    ): ReactiveRedisTemplate<String, VideoProgress> {
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
            .value(Jackson2JsonRedisSerializer(objectMapper, V::class.java))
            .build()

        return ReactiveRedisTemplate(connectionFactory, serializationContext)
    }
}