package com.noljanolja.server.coin_exchange.exception

import com.noljanolja.server.common.exception.BaseException

sealed class Error(
    code: Int,
    message: String,
    cause: Throwable?,
) : BaseException(code, message, cause) {
    object InsufficientCoinBalance : Error(
        400_023,
        "Insufficient coin balance",
        null,
    )
}