package com.noljanolja.server.coin_exchange.repo

import com.noljanolja.server.coin_exchange.model.CoinTransaction
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("coin_transactions")
data class CoinTransactionModel(
    @Id
    @Column("id")
    val id: Long = 0,

    @Column("balance_id")
    val balanceId: Long,

    @Column("amount")
    val amount: Long,

    @Column("reason")
    val reason: String,

    @Column("created_at")
    @CreatedDate
    val createdAt: Instant = Instant.now(),
)

fun CoinTransactionModel.toCoinTransaction() = CoinTransaction(
    id = id,
    balanceId = balanceId,
    amount = amount,
    reason = reason,
    createdAt = createdAt
)