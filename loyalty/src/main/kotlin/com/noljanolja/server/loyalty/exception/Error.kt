package com.noljanolja.server.loyalty.exception

import com.noljanolja.server.common.exception.BaseException

sealed class Error(
    code: Int,
    message: String,
    cause: Throwable?,
) : BaseException(code, message, cause) {
    companion object {
        const val MEMBER_NOT_FOUND = 404_100
        const val INSUFFICIENT_BALANCE = 400_100
    }

    object MemberNotFound : BaseException(
        MEMBER_NOT_FOUND,
        "Member not found",
        null,
    )

    object InsufficientBalance : BaseException(
        INSUFFICIENT_BALANCE,
        "Insufficient balance",
        null
    )
}