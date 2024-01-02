package com.noljanolja.server.consumer.model

data class UserContactDetail (
    val id: String,
    val name: String,
    val avatar: String? = null,
    val phone: String,
    val availablePoints: Long,
    val userTransferPoint: UserTransferPoint?
)