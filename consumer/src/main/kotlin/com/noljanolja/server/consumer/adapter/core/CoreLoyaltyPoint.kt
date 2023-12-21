package com.noljanolja.server.consumer.adapter.core

import com.noljanolja.server.common.utils.REASON_PURCHASE_GIFT
import com.noljanolja.server.consumer.config.language.Translator
import com.noljanolja.server.consumer.model.LoyaltyPoint
import java.time.Instant

data class CoreLoyaltyPoint(
    val id: Long,
    val reason: String,
    val amount: Long,
    val status: Status,
    val createdAt: Instant,
) {
    enum class Status {
        COMPLETED
    }
}

suspend fun CoreLoyaltyPoint.toConsumerLoyaltyPoint(
    translator: Translator,
) = LoyaltyPoint(
    id = id,
    type = if (amount >= 0) LoyaltyPoint.Type.RECEIVED else LoyaltyPoint.Type.PAY,
    unit = if (reason == REASON_PURCHASE_GIFT) LoyaltyPoint.Unit.COIN else LoyaltyPoint.Unit.POINT,
    reason = translator.localize(reason),
    amount = amount,
    createdAt = createdAt,
    status = LoyaltyPoint.Status.valueOf(status.name),
)