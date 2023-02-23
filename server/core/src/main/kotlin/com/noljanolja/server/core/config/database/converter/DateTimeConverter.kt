package com.noljanolja.server.core.config.database.converter

import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toKotlinInstant
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import java.time.LocalDateTime
import java.time.ZoneOffset

@ReadingConverter
internal object LocalDateTimeToInstantConverter : Converter<LocalDateTime, Instant> {
    override fun convert(source: LocalDateTime): Instant {
        return source.atZone(ZoneOffset.UTC).toInstant().toKotlinInstant()
    }
}

@WritingConverter
internal object InstantToLocalDateTimeConverter : Converter<Instant, LocalDateTime> {
    override fun convert(source: Instant): LocalDateTime {
        return LocalDateTime.ofInstant(source.toJavaInstant(), ZoneOffset.UTC)
    }
}
