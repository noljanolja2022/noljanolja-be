package com.noljanolja.server.core.repo.pointtransfer

import com.noljanolja.server.core.model.UserTransferPoint
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("user_transfer_points")
data class UserTransferPointModel (
    @Id
    @Column("id")
    val id: Long = 0,

    @Column("from_user_id")
    val fromUserId: String,

    @Column("to_user_id")
    val toUserId: String,

    @Column("points")
    val points: Long,

    @Column("type")
    val type: UserTransferPoint.Type,

    @Column("created_at")
    @CreatedDate
    val createdAt: Instant = Instant.now(),

    @Column("updated_at")
    @LastModifiedDate
    val updatedAt: Instant = Instant.now(),
)

fun UserTransferPointModel.toUserTransferPoint() = UserTransferPoint(
    id = id,
    fromUserId = fromUserId,
    toUserId = toUserId,
    points = points,
    type = type,
    createdAt = createdAt
)