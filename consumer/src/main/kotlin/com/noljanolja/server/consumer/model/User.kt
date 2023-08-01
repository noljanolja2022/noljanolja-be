package com.noljanolja.server.consumer.model

import java.time.Instant
import java.time.LocalDate

data class SimpleUser(
    val id: String,
    val name: String? = null,
    val avatar: String? = null,
    val phone: String? = null
)

data class User(
    val id: String,
    val name: String? = null,
    val avatar: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val isEmailVerified: Boolean = false,
    val pushToken: String? = null,
    val dob: LocalDate? = null,
    val gender: Gender? = null,
    val preferences: UserPreferences = UserPreferences(),
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
    val referralCode: String = "",
    val referredBy: String = "",
)

data class UserPreferences(
    val collectAndUsePersonalInfo: Boolean? = null
)

enum class Gender {
    MALE,
    FEMALE,
    OTHER
}
