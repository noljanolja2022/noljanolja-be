package com.noljanolja.server.loyalty.repo

import com.noljanolja.server.loyalty.model.Transaction
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("transactions")
data class TransactionModel(
    @Id
    @Column("id")
    val id: Long = 0,

    @Column("member_id")
    val memberId: String,

    @Column("amount")
    val amount: Long,

    @Column("reason")
    val reason: String,

    @Column("created_at")
    @CreatedDate
    val createdAt: Instant = Instant.now(),
)

fun TransactionModel.toTransaction() = Transaction(
    id = id,
    amount = amount,
    reason = reason,
)