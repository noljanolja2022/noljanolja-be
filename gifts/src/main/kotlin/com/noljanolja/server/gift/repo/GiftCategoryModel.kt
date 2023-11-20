package com.noljanolja.server.gift.repo

import com.noljanolja.server.gift.model.GiftCategory
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
    val id: Long,

    @Column("name")
    var name: String = "",

    @Column("image")
    var image: String? = null,

    @Column("created_at")
    @CreatedDate
    val createdAt: Instant = Instant.now(),

    @Column("updated_at")
    @LastModifiedDate
    var updatedAt: Instant = Instant.now(),
)  {

    companion object {
        fun fromGiftCategory(e: GiftCategory): GiftCategoryModel {
            return GiftCategoryModel(
                id = e.id,
                name = e.name ?: "",
            )
        }
    }

    fun toGiftCategory() = GiftCategory(
        id = id,
        name = name,
        image = image
    )
}