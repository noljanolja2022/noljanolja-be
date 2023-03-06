package com.noljanolja.server.auth.model

data class User(
    val id: String,
    val name: String?,
    val avatar: String?,
    val phone: String?,
    val email: String?,
    val isEmailVerified: Boolean,
    val roles: List<Role>,
) {
    enum class Role {
        ADMIN,
        STAFF,
        CONSUMER,
    }
}
