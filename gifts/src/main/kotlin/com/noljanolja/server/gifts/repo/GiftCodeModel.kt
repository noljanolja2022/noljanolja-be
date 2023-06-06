package com.noljanolja.server.gifts.repo

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("gift_codes")
data class GiftCodeModel(
    @Id
    @Column("id")
    val id: Long = 0,

    @Column("code")
    val code: String,

    @Column("gift_id")
    val giftId: Long,

    @Column("user_id")
    var userId: String? = null,

    @Column("created_at")
    @CreatedDate
    val createdAt: Instant = Instant.now(),

    @Column("updated_at")
    @LastModifiedDate
    val updatedAT: Instant = Instant.now(),
)
