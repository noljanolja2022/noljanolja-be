package com.noljanolja.server.core.repo.notification

import com.noljanolja.server.core.model.UserTransferPoint
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("notifications")
data class NotificationModel (
    @Id
    @Column("id")
    val id: Long = 0,

    @Column("user_id")
    val userId: String,

    @Column("title")
    val title: String,

    @Column("type")
    val type: String,

    @Column("body")
    val body: String,

    @Column("image")
    val image: String,

    @Column("data")
    val data: String,

    @Column("is_read")
    var isRead: Boolean,

    @Column("created_at")
    @CreatedDate
    val createdAt: Instant = Instant.now(),

    @Column("updated_at")
    @LastModifiedDate
    val updatedAt: Instant = Instant.now(),
)