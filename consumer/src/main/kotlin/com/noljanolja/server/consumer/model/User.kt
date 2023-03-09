package com.noljanolja.server.consumer.model

import com.noljanolja.server.consumer.utils.serializers.InstantSerializer
import com.noljanolja.server.consumer.utils.serializers.LocalDateSerializer
import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.LocalDate

@Serializable
data class User(
    val id: String,
    val name: String? = null,
    val avatar: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val isEmailVerified: Boolean = false,
    val pushToken: String? = null,
    @Serializable(with = LocalDateSerializer::class)
    val dob: LocalDate? = null,
    val gender: Gender? = null,
    val preferences: UserPreferences = UserPreferences(),
    @Serializable(with=InstantSerializer::class)
    val createdAt: Instant = Instant.now(),
    @Serializable(with=InstantSerializer::class)
    val updatedAt: Instant = Instant.now(),
)

@Serializable
data class UserPreferences(
    val collectAndUsePersonalInfo: Boolean? = null
)

enum class Gender {
    MALE,
    FEMALE,
    OTHER
}
