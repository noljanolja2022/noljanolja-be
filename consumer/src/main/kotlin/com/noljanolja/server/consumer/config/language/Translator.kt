package com.noljanolja.server.consumer.config.language

import com.noljanolja.server.common.filter.RequestHolder
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import java.util.*

@Component
class Translator(
    private val messageSource: MessageSource,
) {
    suspend fun localize(
        code: String,
        params: Array<Any> = emptyArray(),
    ): String {
        val locale = try {
            RequestHolder.getHeaders().awaitFirstOrNull()?.acceptLanguageAsLocales?.firstOrNull()
        } catch (exception: Exception) {
            null
        } ?: Locale("ko")
        return messageSource.getMessage(code, params, locale)
    }
}