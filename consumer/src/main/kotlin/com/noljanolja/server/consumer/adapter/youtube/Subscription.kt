package com.noljanolja.server.consumer.adapter.youtube

data class AddSubscriptionRequest(
    val snippet:AddSubscriptionRequestSnippet
)

data class AddSubscriptionRequestSnippet(
    val resourceId: YoutubeSnippet
)