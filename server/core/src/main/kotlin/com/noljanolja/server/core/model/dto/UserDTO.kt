package com.noljanolja.server.core.model.dto

import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.util.UUID

@Serializable
data class UserDTO(
    val id: String,
    val name: String,
    val avatar: String? = null,
    val isFriend: Boolean? = null
) {
    var displayName: String = ""

    companion object {
//        fun fromUser(
//            user: User,
//            pointInfo: PointInfo = PointInfo(),
//            includePreferences: Boolean = false,
//            isFriend: Boolean? = null
//        ) = with(user) {
//            UserDTO(
//                id = id,
//                addeepId = addeepId,
//                email = email,
//                name = name,
//                gender = gender,
//                dob = dob,
//                phone = phone,
//                avatar = avatar,
//                isFriend = isFriend,
//                allowToSearchByAddeepId = allowToSearchByAddeepId,
//                pointInfo = PointInfoDTO.fromPointInfo(pointInfo),
//                preferences = preferences.takeIf { includePreferences && preferences.isNotBlank() }
//                    ?.let { Json.decodeFromString(it) }
//            ).apply {
//                displayName = user.displayName
//            }
//        }
    }
}