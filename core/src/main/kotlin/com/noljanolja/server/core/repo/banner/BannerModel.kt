package com.noljanolja.server.core.repo.banner

import com.noljanolja.server.core.model.Banner
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("banners")
data class BannerModel(
    @Id
    @Column("id")
    val id: Long = 0,

    @Column("title")
    var title: String = "",

    @Column("description")
    var description: String = "",

    @Column("content")
    var content: String = "",

    @Column("image")
    var image: String = "",

    @Column("is_active")
    var isActive: Boolean = true,

    @Column("priority")
    var priority: Banner.BannerPriority = Banner.BannerPriority.LOW,

    @Column("action")
    var action: Banner.BannerAction = Banner.BannerAction.NONE,

    @Column("start_time")
    var startTime: Instant = Instant.now(),

    @Column("end_time")
    var endTime: Instant = Instant.now(),

    @Column("created_at")
    @CreatedDate
    val createdAt: Instant = Instant.now(),

    @Column("updated_at")
    @LastModifiedDate
    val updatedAt: Instant = Instant.now(),
)

fun BannerModel.toBanner() = Banner(
    id = id,
    title = title,
    description = description,
    content = content,
    image = image,
    isActive = isActive,
    priority = priority,
    action = action,
    startTime = startTime,
    endTime = endTime,
)