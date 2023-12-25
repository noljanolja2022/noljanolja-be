package com.noljanolja.server.loyalty.exception

import com.noljanolja.server.common.exception.BaseException

sealed class Error(
    code: Int,
    message: String,
    cause: Throwable?,
) : BaseException(code, message, cause) {
    companion object {
        const val MEMBER_NOT_FOUND = 404_008
        const val INSUFFICIENT_BALANCE = 400_100
        const val TRANSACTION_NOT_FOUND = 404_009
    }

    object TransactionNotFound : BaseException (
        TRANSACTION_NOT_FOUND,
        "Transaction not found",
        null
    )

    object MemberNotFound : BaseException(
        MEMBER_NOT_FOUND,
        "Member not found",
        null,
    )

    object InsufficientPointBalance : BaseException(
        INSUFFICIENT_BALANCE,
        "Insufficient point balance",
        null
    )
}