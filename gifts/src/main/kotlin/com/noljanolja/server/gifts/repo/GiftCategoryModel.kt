package com.noljanolja.server.gifts.repo

import com.noljanolja.server.gifts.model.Gift
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("gift_categories")
data class GiftCategoryModel(
    @Id
    @Column("id")
    val id: Long = 0,

    @Column("code")
    val code: String = "",

    @Column("image")
    val image: String = "",

    @Column("created_at")
    @CreatedDate
    val createdAt: Instant = Instant.now(),

    @Column("updated_at")
    @LastModifiedDate
    val updatedAt: Instant = Instant.now(),
)

fun GiftCategoryModel.toGiftCategory() = Gift.Category(
    id = id,
    code = code,
    image = image,
)