package com.noljanolja.server.consumer.adapter.auth

import com.fasterxml.jackson.annotation.JsonIgnore

data class AuthUser(
    val id: String,
    val name: String?,
    val avatar: String?,
    val phone: String?,
    val email: String?,
    val isEmailVerified: Boolean,
) {
    @JsonIgnore
    var bearerToken: String = ""
}
