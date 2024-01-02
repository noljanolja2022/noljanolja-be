package com.noljanolja.server.consumer.adapter.core

import com.noljanolja.server.consumer.model.UserContactDetail

data class CoreUserContactDetail(
    val id: String,
    val name: String,
    val avatar: String? = null,
    val phone: String,
    val availablePoints: Long,
    val userTransferPoint: CoreUserTransferPoint?
)

fun CoreUserContactDetail.toUserContactDetail() = UserContactDetail(
    id = id,
    name = name,
    avatar = avatar,
    phone = phone,
    availablePoints = availablePoints,
    userTransferPoint = userTransferPoint?.toUserTransferPoint()
)