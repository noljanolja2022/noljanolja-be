package com.noljanolja.server.gift.repo

import com.noljanolja.server.gift.model.Gift
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Transient
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("gifts")
data class GiftModel(
    @Id
    @Column("id")
    val id: Long = 0,

    @Column("name")
    var name: String,

    @Column("description")
    var description: String,

    @Column("image")
    var image: String,

    @Column("start_time")
    var startTime: Instant,

    @Column("end_time")
    var endTime: Instant,

    @Column("category_id")
    val categoryId: Long,

    @Column("brand_id")
    val brandId: Long,

    @Column("total")
    val total: Int,

    @Column("price")
    var price: Long,

    @Column("remaining")
    var remaining: Int,

    @Column("max_buy_times")
    var maxBuyTimes: Int = 0,

    @Column("created_at")
    @CreatedDate
    val createdAt: Instant = Instant.now(),

    @Column("updated_at")
    @LastModifiedDate
    val updatedAt: Instant = Instant.now(),
) {
    @Transient
    var category: GiftCategoryModel = GiftCategoryModel()

    @Transient
    var brand: GiftBrandModel = GiftBrandModel()

    @Transient
    var codes: List<String> = emptyList()
}

fun GiftModel.toGift() = Gift(
    id = id,
    name = name,
    description = description,
    image = image,
    startTime = startTime,
    endTime = endTime,
    total = total,
    remaining = remaining,
    brand = brand.toGiftBrand(),
    category = category.toGiftCategory(),
    codes = codes,
    price = price,
    maxBuyTimes = maxBuyTimes,
)
