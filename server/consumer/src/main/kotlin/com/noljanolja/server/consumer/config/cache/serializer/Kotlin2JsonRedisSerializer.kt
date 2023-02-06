package com.noljanolja.server.consumer.config.cache.serializer

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import org.springframework.data.redis.serializer.RedisSerializer
import org.springframework.data.redis.serializer.SerializationException
import java.io.ByteArrayOutputStream

@ExperimentalSerializationApi
class Kotlin2JsonRedisSerializer<T : Any>(
    private val json: Json = Json,
    private val serializer: KSerializer<T>,
) : RedisSerializer<T> {

    override fun serialize(t: T?): ByteArray? {
        if (t == null) return byteArrayOf()

        try {
            val output = ByteArrayOutputStream()
            json.encodeToStream(serializer, t, output)
            return output.toByteArray()
        } catch (error: Throwable) {
            throw SerializationException("Could not write JSON: ${error.message}", error)
        }
    }

    override fun deserialize(bytes: ByteArray?): T? {
        if (bytes == null || bytes.isEmpty()) return null

        try {
            return json.decodeFromStream(serializer, bytes.inputStream())
        } catch (error: Throwable) {
            throw SerializationException("Could not read JSON: ${error.message}", error)
        }
    }
}