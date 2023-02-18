package com.noljanolja.server.common.model

import kotlinx.serialization.Serializable

@Serializable
data class TokenData(
    val userId: String,
) {
    @Transient
    var bearerToken: String = ""
}