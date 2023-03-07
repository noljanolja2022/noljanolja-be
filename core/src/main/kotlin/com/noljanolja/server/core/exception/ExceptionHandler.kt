package com.noljanolja.server.core.exception

import com.noljanolja.server.common.rest.BaseExceptionsHandler
import org.springframework.boot.autoconfigure.web.WebProperties
import org.springframework.boot.web.reactive.error.ErrorAttributes
import org.springframework.context.ApplicationContext
import org.springframework.core.annotation.Order
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.stereotype.Component

const val EXCEPTION_HANDLER_ORDER = -2

@Component
@Order(EXCEPTION_HANDLER_ORDER)
class ExceptionHandler(
    errorAttributes: ErrorAttributes,
    webProperties: WebProperties,
    applicationContext: ApplicationContext,
    configurer: ServerCodecConfigurer,
) : BaseExceptionsHandler(errorAttributes, webProperties, applicationContext, configurer)
