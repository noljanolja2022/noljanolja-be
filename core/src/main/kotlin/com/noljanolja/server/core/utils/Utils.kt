package com.noljanolja.server.core.utils

import com.google.i18n.phonenumbers.PhoneNumberUtil

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