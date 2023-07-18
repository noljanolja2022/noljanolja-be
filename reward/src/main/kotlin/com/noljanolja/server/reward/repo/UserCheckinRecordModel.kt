package com.noljanolja.server.reward.repo

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("user_checkin_records")
data class UserCheckinRecordModel(
    @Id
    @Column("id")
    val id: Long = 1,

    @Column("day")
    val day: Int,

    @Column("user_id")
    val userId: String,

    @Column("created_at")
    @CreatedDate
    val createdAt: Instant = Instant.now(),

    @Column("updated_at")
    @LastModifiedDate
    val updatedAt: Instant = Instant.now(),
)
