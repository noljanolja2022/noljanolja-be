package com.noljanolja.server.core.config.database

import com.noljanolja.server.core.config.database.converter.ByteArrayToUUIDConverter
import com.noljanolja.server.core.config.database.converter.InstantToLocalDateTimeConverter
import com.noljanolja.server.core.config.database.converter.LocalDateTimeToInstantConverter
import com.noljanolja.server.core.config.database.converter.UUIDToByteArrayConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions
import org.springframework.data.r2dbc.dialect.MySqlDialect

@Configuration
class DatabaseConfig {

    @Bean
    fun customConversions(): R2dbcCustomConversions {
        return R2dbcCustomConversions.of(
            MySqlDialect.INSTANCE, mutableListOf(
                ByteArrayToUUIDConverter,
                UUIDToByteArrayConverter,
                LocalDateTimeToInstantConverter,
                InstantToLocalDateTimeConverter,
            )
        )
    }
}