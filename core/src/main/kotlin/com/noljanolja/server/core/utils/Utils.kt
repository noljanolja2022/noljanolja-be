package com.noljanolja.server.core.utils

import com.google.i18n.phonenumbers.PhoneNumberUtil
import kotlin.random.Random

const val KOREAN_COUNTRY_CODE = 82

fun parsePhoneNumber(
    phone: String?,
    fallbackCountryCode: Int = KOREAN_COUNTRY_CODE,
) =
    try {
        PhoneNumberUtil.getInstance().let {
            it.parse(phone, it.getRegionCodeForCountryCode(fallbackCountryCode))
        }
    } catch (e: Exception) {
        null
    }

fun genRandomString(numDigits: Int = 0, numWords: Int = 0): String = when {
    numDigits > 0 && (Random.nextBoolean() || numWords == 0)
    -> ('0'..'9').random() + genRandomString(numDigits - 1, numWords)

    numWords > 0 -> ('A'..'Z').random() + genRandomString(numDigits, numWords - 1)
    else -> ""
}

fun genReferralCode() = genRandomString(numDigits = 3, numWords = 4)