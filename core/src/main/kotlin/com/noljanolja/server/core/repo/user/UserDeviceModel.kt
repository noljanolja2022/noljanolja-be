package com.noljanolja.server.core.repo.user

import com.noljanolja.server.core.model.DeviceType
import com.noljanolja.server.core.model.UserDevice
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("user_devices")
data class UserDeviceModel(
    @Id
    @Column("id")
    val id: Long,

    @Column("user_id")
    val userId: String,

    @Column("device_type")
    val deviceType: String,

    @Column("device_token")
    val deviceToken: String,

    @Column("created_at")
    @CreatedDate
    val createdAt: Instant = Instant.now(),

    @Column("updated_at")
    @LastModifiedDate
    val updatedAt: Instant = Instant.now(),
) {
    companion object {
        fun UserDevice.toUserDeviceModel() = UserDeviceModel(
            id = id,
            userId = userId,
            deviceType = deviceType.name,
            deviceToken = deviceToken,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )
    }
}

fun UserDeviceModel.toUserDevice() = UserDevice(
    id = id,
    userId = userId,
    deviceType = DeviceType.valueOf(deviceType),
    deviceToken = deviceToken,
    createdAt = createdAt,
    updatedAt = updatedAt,
)


