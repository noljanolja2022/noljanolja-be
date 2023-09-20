package com.noljanolja.server.consumer.adapter.core

import com.noljanolja.server.consumer.config.language.Translator
import com.noljanolja.server.consumer.model.CoinTransaction
import java.time.Instant

data class CoreCoinTransaction(
    val id: Long,
    val balanceId: Long,
    val reason: String,
    val amount: Double,
    val status: Status = Status.COMPLETED,
    val createdAt: Instant,
) {
    enum class Status {
        COMPLETED,
    }

    enum class Type {
        SPENT,
        RECEIVED
    }
}

suspend fun CoreCoinTransaction.toCoinTransaction(
    translator: Translator
) = CoinTransaction(
    id = id,
    balanceId = balanceId,
    reason = translator.localize(reason),
    amount = amount,
    status = CoinTransaction.Status.valueOf(status.name),
    createdAt = createdAt,
)