package com.noljanolja.server.gift.exception

import com.noljanolja.server.common.exception.BaseException

sealed class Error(
    code: Int,
    message: String,
    cause: Throwable?,
) : BaseException(code, message, cause) {
    companion object {
        const val GIFT_NOT_FOUND = 404_010
        const val CATEGORY_NOT_FOUND = 404_011
        const val BRAND_NOT_FOUND = 404_012
        const val NOT_ENOUGH_GIFT = 400_015
        const val MAXIMUM_BUY_TIMES_REACHED = 400_016
    }

    object GiftNotFound : Error(
        GIFT_NOT_FOUND,
        "Gift not found",
        null,
    )

    object CategoryNotFound : Error(
        CATEGORY_NOT_FOUND,
        "Category not found",
        null
    )

    object BrandNotFound : Error(
        BRAND_NOT_FOUND,
        "Brand not found",
        null,
    )

    object NotEnoughGift : Error(
        NOT_ENOUGH_GIFT,
        "Not enough gift",
        null,
    )

    object MaximumBuyTimesReached : Error(
        MAXIMUM_BUY_TIMES_REACHED,
        "Maximum buy times reached",
        null,
    )
}