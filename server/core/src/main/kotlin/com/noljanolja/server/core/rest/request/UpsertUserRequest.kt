package com.noljanolja.server.core.rest.request

import com.noljanolja.server.common.validator.StringValidator
import com.noljanolja.server.common.validator.Validator
import com.noljanolja.server.common.validator.validate
import kotlinx.serialization.Serializable

@Serializable
data class UpsertUserRequest (
    val firebaseUserId: String,
    val name: String,
    val profileImage: String,
    val pushToken: String,
    val pushNotiEnabled: Boolean,
) {
    init {
        mutableListOf<Validator>().apply {
            add(StringValidator(
                obj = firebaseUserId,
                fieldName = "firebaseUseId",
                required = true,
            ))
            add(StringValidator(
                obj = name,
                fieldName = "name",
                required = true,
            ))
            add(StringValidator(
                obj = profileImage,
                fieldName = "profileImage",
                required = true,
            ))
            add(StringValidator(
                obj = pushToken,
                fieldName = "pushToken",
                required = true,
            ))
        }.validate()
    }
}