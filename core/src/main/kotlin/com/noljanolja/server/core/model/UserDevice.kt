package com.noljanolja.server.core.model

import java.time.Instant

data class UserDevice(
    val id: Long,
    val userId: String,
    val deviceType: DeviceType,
    val deviceToken: String,
    val createdAt: Instant = Instant.now(),
    var updatedAt: Instant = Instant.now(),
)

enum class DeviceType {
    Mobile,
    Desktop
}
