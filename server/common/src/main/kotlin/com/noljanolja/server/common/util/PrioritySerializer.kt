package com.noljanolja.server.common.util

import com.noljanolja.server.common.model.CoreAnnouncement
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object PrioritySerializer : KSerializer<CoreAnnouncement.Priority> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Priority", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: CoreAnnouncement.Priority) {
        encoder.encodeString(value.name.lowercase())
    }

    override fun deserialize(decoder: Decoder): CoreAnnouncement.Priority {
        return enumByNameIgnoreCase(decoder.decodeString())
    }
}