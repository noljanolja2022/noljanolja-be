package com.noljanolja.server.admin.adapter.core

import com.noljanolja.server.admin.model.User
import java.time.Instant

data class CoreUser(
    val id: String,
    val name: String = "",
    val avatar: String? = null,
    val phone: String = "",
    val email: String? = null,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
)

fun CoreUser.toAdminUser() = User(
    id = id,
    name = name,
    avatar = avatar,
    phone = phone,
    email = email,
    createdAt = createdAt,
    updatedAt = updatedAt,
)