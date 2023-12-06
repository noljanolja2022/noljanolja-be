package com.noljanolja.server.gift.repo

import com.noljanolja.server.gift.model.Gift
import com.noljanolja.server.gift.model.GiftBrand
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

    @Column("category_id")
    var categoryId: Long? = null,

    @Column("limit_day")
    val limitDay: Int = 1,

    @Column("price")
    var price: Long,

    @Column("retail_price")
    var retailPrice: Long,

    @Column("is_active")
    var isActive: Boolean,

    @Column("is_featured")
    var isFeatured: Boolean,

    @Column("is_today_offer")
    var isTodayOffer: Boolean,

    @Column("created_at")
    @CreatedDate
    val createdAt: Instant = Instant.now(),

    @Column("updated_at")
    @LastModifiedDate
    val updatedAt: Instant = Instant.now(),

    @Column("locale")
    val locale: String?
) : Persistable<String> {
    @Transient
    var isNewRecord = false
    override fun getId() = _id

    override fun isNew() = isNewRecord

    companion object {
        fun fromGift(eg: GiftModel?, e: Gift, locale: String?): GiftModel {
            return GiftModel(
                _id = e.id,
                giftNo = e.giftNo,
                name = eg?.name ?: e.name,
                description = eg?.description ?: e.description,
                image = eg?.image ?: e.image,
                endTime = e.endTime,
                brandId = e.brand.id,
                price = eg?.price ?: e.price,
                retailPrice = eg?.retailPrice ?: e.retailPrice,
                isActive = eg?.isActive ?: false,
                createdAt = eg?.createdAt ?: Instant.now(),
                limitDay = e.limitDay,
                isFeatured = eg?.isFeatured ?: false,
                isTodayOffer = eg?.isTodayOffer ?: false,
                locale = locale
            ).apply {
                isNewRecord = eg == null
            }
        }
    }
}

fun GiftModel.toGift(brandModel: GiftBrandModel? = null, categoryModel: GiftCategoryModel? = null) = Gift(
    id = _id,
    giftNo = giftNo,
    name = name,
    description = description,
    image = image,
    endTime = endTime,
    brand = brandModel?.toGiftBrand() ?: GiftBrand(id = brandId),
    category = categoryModel?.toGiftCategory(),
    price = price,
    retailPrice = retailPrice,
    limitDay = limitDay,
    isActive = isActive,
    isFeatured = isFeatured,
    isTodayOffer = isTodayOffer
)
