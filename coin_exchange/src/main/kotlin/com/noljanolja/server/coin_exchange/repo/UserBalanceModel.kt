package com.noljanolja.server.coin_exchange.repo

import com.noljanolja.server.coin_exchange.model.UserBalance
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("user_balances")
data class UserBalanceModel(
    @Id
    @Column("id")
    val id: Long = 0,

    @Column("user_id")
    val userId: String,

    @Column("balance")
    var balance: Long = 0,

    @Column("created_at")
    @CreatedDate
    val createdAt: Instant = Instant.now(),

    @Column("updated_at")
    @LastModifiedDate
    val updatedAt: Instant = Instant.now(),
)

fun UserBalanceModel.toUserBalance() = UserBalance(
    id = id,
    balance = balance,
    createdAt = createdAt,
)