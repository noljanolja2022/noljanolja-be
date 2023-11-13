package com.noljanolja.server.gift.repo

import com.noljanolja.server.gift.model.Gift
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Transient
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("gifts")
data class GiftModel(
    @Id
    @Column("id")
    var _id: String,

    @Column("gift_no")
    val giftNo: Long = 0,

    @Column("name")
    var name: String,

    @Column("description")
    var description: String,

    @Column("image")
    var image: String,

    @Column("end_time")
    var endTime: Instant,

    @Column("brand_id")
    val brandId: String,

    @Column("price")
    var price: Long,

    @Column("retail_price")
    var retailPrice: Long,

    @Column("is_active")
    val isActive: Boolean,

    @Column("created_at")
    @CreatedDate
    val createdAt: Instant = Instant.now(),

    @Column("updated_at")
    @LastModifiedDate
    val updatedAt: Instant = Instant.now(),
) : Persistable<String> {
    @Transient
    var isNewRecord = false
    override fun getId() = _id

    override fun isNew() = isNewRecord

    @Transient
    var brand: GiftBrandModel = GiftBrandModel(brandId)

    companion object {
        fun fromGift(e: Gift): GiftModel {
            return GiftModel(
                _id = e.id,
                giftNo = e.giftNo,
                name = e.name,
                description = e.description,
                image = e.image,
                endTime = e.endTime,
                brandId = e.brand.id,
                price = e.price,
                retailPrice = e.price,
                isActive = false
            ).apply {
                isNewRecord = true
            }
        }
    }
}

fun GiftModel.toGift() = Gift(
    id = _id,
    giftNo = giftNo,
    name = name,
    description = description,
    image = image,
    endTime = endTime,
    brand = brand.toGiftBrand(),
    price = price,
    retailPrice = retailPrice,
    isActive = isActive
)
