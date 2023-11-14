package com.noljanolja.server.coin_exchange.repo

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("exchange_rate")
data class ExchangeRateModel(
    @Id
    @Column("id")
    val id: Long = 0,

    @Column("coin")
    var coin: Int = 0,

    @Column("point")
    var point: Int = 0,

    @Column("reward_recurring_amount")
    var rewardRecurringAmount: Int = 0,

    @Column("created_at")
    @CreatedDate
    val createdAt: Instant = Instant.now(),

    @Column("updated_at")
    @LastModifiedDate
    val updatedAt: Instant = Instant.now(),
)