package com.noljanolja.server.gift.repo

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
@Table("gift_transactions")
data class GiftTransactionModel(
    @Id
    @Column("id")
    val _id: String = "",

    @Column("gift_id")
    var giftCode: String,

    @Column("user_id")
    var userId: String,

    @Column("order_no")
    var orderNo: String,

    @Column("pin_no")
    var pinNumber: String? = null,

    @Column("bar_code")
    var barCode: String? = null,

    @Column("price")
    var price: Double,

    @Column("created_at")
    @CreatedDate
    val createdAt: Instant = Instant.now(),

    ) : Persistable<String> {
    @Transient
    var isNewRecord = false
    override fun getId() = _id

    override fun isNew() = isNewRecord
}