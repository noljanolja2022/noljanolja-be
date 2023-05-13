package com.noljanolja.server.admin.model

import java.time.Instant

data class User(
    val id: String,
    val name: String,
    val avatar: String? = null,
    val phone: String,
    val email: String? = null,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
)

data class CreateUserRequest(
    val userName: String,
    val password: String,
    val role: String = "admin"
)