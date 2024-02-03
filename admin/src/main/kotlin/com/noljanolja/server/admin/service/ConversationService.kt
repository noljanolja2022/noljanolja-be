package com.noljanolja.server.admin.service

import com.noljanolja.server.admin.adapter.core.CoreApi
import com.noljanolja.server.admin.model.ConversationAnalytics
import org.springframework.stereotype.Component

@Component
class ConversationService(
    private val coreApi: CoreApi
) {
    suspend fun getConversationAnalytics(): ConversationAnalytics {
        return coreApi.getConversationAnalytics()
    }
}