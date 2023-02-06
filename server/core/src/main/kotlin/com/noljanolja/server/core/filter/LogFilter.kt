package com.noljanolja.server.core.filter

import com.noljanolja.server.common.rest.BaseLogFilter
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class LogFilter : BaseLogFilter()