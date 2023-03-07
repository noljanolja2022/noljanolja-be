package com.noljanolja.server.consumer.model

import java.time.Instant
import java.time.LocalDate

data class User(
    val id: String,
    val name: String,
    val avatar: String? = null,
    val phone: String,
    val email: String? = null,
    val isEmailVerified: Boolean = false,
    val dob: LocalDate? = null,
    val gender: Gender? = null,
    val preferences: UserPreferences = UserPreferences(),
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
)

data class UserPreferences(
    val collectAndUsePersonalInfo: Boolean? = null
)

enum class Gender {
    MALE,
    FEMALE,
    OTHER
}
