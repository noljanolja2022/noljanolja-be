package com.noljanolja.server.gift.repo

import com.noljanolja.server.gift.model.Gift
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("gift_brands")
data class GiftBrandModel(
    @Id
    @Column("id")
    val id: Long = 0,

    @Column("name")
    var name: String = "",

    @Column("image")
    var image: String = "",

    @Column("created_at")
    @CreatedDate
    val createdAt: Instant = Instant.now(),

    @Column("updated_at")
    @LastModifiedDate
    val updatedAt: Instant = Instant.now(),
)

fun GiftBrandModel.toGiftBrand() = Gift.Brand(
    id = id,
    name = name,
    image = image,
)
