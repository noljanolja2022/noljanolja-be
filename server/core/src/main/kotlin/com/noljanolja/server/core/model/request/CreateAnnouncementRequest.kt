package com.noljanolja.server.core.model.request

import com.noljanolja.server.core.model.CoreAnnouncement
import com.noljanolja.server.core.util.PrioritySerializer
import com.noljanolja.server.common.validator.StringValidator
import com.noljanolja.server.common.validator.Validator
import com.noljanolja.server.common.validator.validate
import kotlinx.serialization.Serializable

@Serializable
data class CreateAnnouncementRequest(
    val title: String,
    val content: String,
    @Serializable(with = PrioritySerializer::class)
    val priority: CoreAnnouncement.Priority,
) {
    init {
        mutableListOf<Validator>().apply {
            add(
                StringValidator(
                    obj = title,
                    fieldName = title,
                )
            )
            add(
                StringValidator(
                    obj = content,
                    fieldName = content,
                )
            )
        }.validate()
    }
}