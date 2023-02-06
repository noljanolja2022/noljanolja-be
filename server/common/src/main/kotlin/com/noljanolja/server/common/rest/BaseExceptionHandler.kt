package com.noljanolja.server.common.rest

import com.noljanolja.server.common.exception.BaseException
import com.noljanolja.server.common.exception.DefaultBadRequestException
import com.noljanolja.server.common.exception.DefaultInternalErrorException
import com.noljanolja.server.common.exception.DefaultNotFoundException
import org.springframework.boot.autoconfigure.web.WebProperties
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler
import org.springframework.boot.web.reactive.error.ErrorAttributes
import org.springframework.context.ApplicationContext
import org.springframework.http.HttpStatus
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.web.reactive.function.server.*
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerWebInputException

abstract class BaseExceptionsHandler(
    errorAttributes: ErrorAttributes,
    webProperties: WebProperties,
    applicationContext: ApplicationContext,
    serverCodecConfigurer: ServerCodecConfigurer,
) : AbstractErrorWebExceptionHandler(
    errorAttributes, webProperties.resources, applicationContext
) {

    companion object {
        const val HTTP_STATUS_FACTOR = 1000
    }

    init {
        this.setMessageWriters(serverCodecConfigurer.writers)
        this.setMessageReaders(serverCodecConfigurer.readers)
    }

    override fun getRoutingFunction(errorAttributes: ErrorAttributes) = coRouter {
        RequestPredicates.all().invoke { request ->
            val error = errorAttributes.getError(request) as Exception
            createErrorResponse(request, error)
        }
    }

    open suspend fun createErrorResponse(
        request: ServerRequest,
        error: Exception,
    ): ServerResponse {
        val exception = when (error) {
            is ServerWebInputException -> DefaultBadRequestException(error.cause)

            is ResponseStatusException -> if (error.status == HttpStatus.NOT_FOUND) {
                DefaultNotFoundException(error.cause)
            } else {
                DefaultInternalErrorException(error.cause)
            }

            is BaseException -> error

            else -> DefaultInternalErrorException(error.cause)
        }

        val status = HttpStatus.valueOf(exception.code / HTTP_STATUS_FACTOR)

        return ServerResponse.status(status)
            .bodyValueAndAwait(
                Response<Nothing>(
                    code = exception.code,
                    message = exception.message,
                )
            )
    }
}