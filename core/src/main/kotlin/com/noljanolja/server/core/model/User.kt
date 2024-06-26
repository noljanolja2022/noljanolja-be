package com.noljanolja.server.core.model

import java.time.Instant
import java.time.LocalDate

data class User(
    val id: String,
    val name: String,
    val avatar: String? = null,
    val phone: String,
    val email: String? = null,
    val dob: LocalDate? = null,
    val gender: Gender? = null,
    val isActive: Boolean = true,
    val isReported: Boolean = false,
    val isBlocked: Boolean = false,
    val preferences: UserPreferences = UserPreferences(),
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
    val referralCode: String = "",
    val referredBy: String = "",
)

data class UserContactDetail(
    val id: String,
    val name: String,
    val avatar: String? = null,
    val phone: String,
    val availablePoints: Long,
    val userTransferPoint: UserTransferPoint?
)

data class UserPreferences(
    val collectAndUsePersonalInfo: Boolean? = null
)

enum class Gender {
    MALE,
    FEMALE,
    OTHER
}