package com.noljanolja.server.core.repo.sticker

import com.noljanolja.server.core.model.Sticker
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("stickers")
data class StickerModel(
    @Id
    @Column("id")
    val id: Long = 0,

    @Column("pack_id")
    val packId: Long = 0,

    @Column("image_file")
    val imageFile: String = "",

    @Column("emojis")
    val emojis: String = "",

    @Column("created_at")
    @CreatedDate
    val createdAt: Instant = Instant.now(),

    @Column("updated_at")
    @LastModifiedDate
    val updatedAt: Instant = Instant.now(),
) {
    fun toSticker() : Sticker {
        return Sticker(
            imageFile = imageFile,
            emojis = emojis.split(",")
        )
    }
    companion object {
        fun fromSticker(st: Sticker, packId: Long) : StickerModel {
            return StickerModel(
                id = 0,
                packId = packId,
                imageFile = st.imageFile,
                emojis = st.emojis.joinToString(","),
            )
        }
    }
}