package com.noljanolja.server.admin.rest.request

import com.noljanolja.server.admin.model.CheckinConfig

data class UpsertCheckinConfigRequest(
    val configs: List<CheckinConfig>,
)