package com.noljanolja.server.coin_exchange.repo

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("exchange_rate")
class ExchangeRateModel(
    @Id
    @Column("id")
    val id: Long,

    @Column("coin_to_point_rate")
    var coinToPointRate: Double = 0.0,

    @Column("created_at")
    @CreatedDate
    val createdAt: Instant = Instant.now(),

    @Column("updated_at")
    @LastModifiedDate
    val updatedAt: Instant = Instant.now(),
)