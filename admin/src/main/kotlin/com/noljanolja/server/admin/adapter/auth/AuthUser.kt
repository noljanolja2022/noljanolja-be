package com.noljanolja.server.admin.adapter.auth

import com.fasterxml.jackson.annotation.JsonIgnore

data class AuthUser(
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
    @JsonIgnore
    var bearerToken: String = ""

}
