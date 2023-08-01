package com.noljanolja.server.consumer.adapter.core

import com.noljanolja.server.consumer.model.Gender
import com.noljanolja.server.consumer.model.User
import com.noljanolja.server.consumer.model.UserPreferences
import java.time.Instant
import java.time.LocalDate

data class CoreUser(
    val id: String,
    val name: String? = null,
    val avatar: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val dob: LocalDate? = null,
    val gender: Gender? = null,
    val preferences: CoreUserPreferences = CoreUserPreferences(),
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
    val referralCode: String = "",
    val referredBy: String = "",
)

data class CoreUserPreferences(
    val collectAndUsePersonalInfo: Boolean? = null,
)

fun CoreUser.toConsumerUser() = User(
    id = id,
    name = name,
    avatar = avatar,
    phone = phone,
    email = email,
    dob = dob,
    gender = gender,
    preferences = preferences.toConsumerUserPreferences(),
    createdAt = createdAt,
    updatedAt = updatedAt,
    referredBy = referredBy,
    referralCode = referralCode,
)

fun CoreUserPreferences.toConsumerUserPreferences() = UserPreferences(
    collectAndUsePersonalInfo = collectAndUsePersonalInfo,
)
