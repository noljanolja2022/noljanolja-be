package com.noljanolja.server.gift.repo

import com.noljanolja.server.gift.model.GiftBrand
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("gift_brands")
data class GiftBrandModel(
    @Id
    @Column("id")
    val _id: String,

    @Column("name")
    var name: String = "",

    @Column("image")
    var image: String = "",
) : Persistable<String> {
    @Transient
    var isNewRecord = false
    override fun getId() = _id

    override fun isNew() = isNewRecord
    companion object {
        fun fromGiftBrand(e: GiftBrand): GiftBrandModel {
            return GiftBrandModel(
                _id = e.id,
                name = e.name,
                image = e.image
            ).apply {
                isNewRecord = true
            }
        }

        fun fromGiftBrandList(giftBrands: List<GiftBrand>): List<GiftBrandModel> {
            return giftBrands.map { fromGiftBrand(it) }
        }
    }

    fun toGiftBrand() = GiftBrand(
        id = _id,
        name = name,
        image = image,
    )
}


