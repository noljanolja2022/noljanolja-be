package com.noljanolja.server.core.utils

import com.google.i18n.phonenumbers.PhoneNumberUtil

const val KOREAN_COUNTRY_CODE = 82
fun parsePhoneNumber(phone: String?) =
    try {
        PhoneNumberUtil.getInstance().let {
            it.parse(phone, it.getRegionCodeForCountryCode(KOREAN_COUNTRY_CODE))
        }
    } catch (e: Exception) {
        null
    }