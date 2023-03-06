package com.noljanolja.server.core.model

import java.time.Instant

data class UserContact(
    val id: Long,
    val name: String,
    val phone: String?,
    val email: String?,
    val createdAt: Instant = Instant.now(),
    var updatedAt: Instant = Instant.now(),
)
