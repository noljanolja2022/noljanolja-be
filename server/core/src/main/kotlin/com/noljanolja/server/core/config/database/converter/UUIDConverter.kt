package com.noljanolja.server.core.config.database.converter

import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import java.nio.ByteBuffer
import java.util.*

@ReadingConverter
internal object ByteArrayToUUIDConverter : Converter<ByteArray, UUID> {
    override fun convert(source: ByteArray): UUID {
        val bb = ByteBuffer.wrap(source)
        return UUID(bb.long, bb.long)
    }
}

@WritingConverter
internal object UUIDToByteArrayConverter : Converter<UUID, ByteArray> {
    override fun convert(uuid: UUID): ByteArray {
        return ByteBuffer.wrap(ByteArray(16)).apply {
            putLong(uuid.mostSignificantBits)
            putLong(uuid.leastSignificantBits)
        }.array()
    }
}
