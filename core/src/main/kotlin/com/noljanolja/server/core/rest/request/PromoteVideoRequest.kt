package com.noljanolja.server.core.rest.request

import java.time.LocalDate

data class PromoteVideoRequest (
    val startDate: LocalDate,
    val endDate: LocalDate,
)