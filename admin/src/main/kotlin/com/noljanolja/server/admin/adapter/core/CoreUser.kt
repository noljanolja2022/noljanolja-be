package com.noljanolja.server.admin.adapter.core

import com.noljanolja.server.admin.model.User
import java.time.Instant
import java.time.LocalDate

data class CoreUser(
    val id: String,
    val name: String = "",
    val avatar: String? = null,
    val phone: String = "",
    val email: String? = null,
    val dob: LocalDate? = null,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
    val gender: Gender? = null,
    val isActive: Boolean = false,
    val isBlocked: Boolean = false
)

enum class Gender {
    MALE,
    FEMALE,
    OTHER
}

fun CoreUser.toAdminUser() = User(
    id = id,
    name = name,
    avatar = avatar,
    phone = phone,
    email = email,
    dob = dob,
    createdAt = createdAt,
    updatedAt = updatedAt,
    isActive = isActive
)