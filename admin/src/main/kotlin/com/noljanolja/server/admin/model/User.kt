package com.noljanolja.server.admin.model

import java.time.Instant
import java.time.LocalDate

data class User(
    val id: String,
    val name: String,
    val avatar: String? = null,
    val phone: String,
    val email: String? = null,
    val dob: LocalDate? = null,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
    val isActive: Boolean = true,
)

data class CreateUserRequest(
    val userName: String,
    val password: String,
    val role: String = "admin"
)

data class UserActivationRequest(
    val isActive: Boolean,
)

data class CoreUserUpdateReq(
    val isActive: Boolean? = null,
    val isDeleted: Boolean? = null
)