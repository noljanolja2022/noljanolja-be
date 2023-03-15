package com.noljanolja.server.auth.model

data class User(
    val id: String,
    val name: String? = null,
    val avatar: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val isEmailVerified: Boolean = false,
    val roles: List<Role> = emptyList(),
) {
    enum class Role {
        ADMIN,
        STAFF,
        CONSUMER,
    }
}
