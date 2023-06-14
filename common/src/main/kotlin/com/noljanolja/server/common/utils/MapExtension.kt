package com.noljanolja.server.common.utils

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.codec.multipart.FilePart
import org.springframework.http.codec.multipart.FormFieldPart
import org.springframework.http.codec.multipart.Part
import org.springframework.util.MultiValueMap

fun MultiValueMap<String, Part>.getFieldPartValue(fieldValue: String): String? {
    return (this[fieldValue]?.firstOrNull() as? FormFieldPart)?.value()
}

fun MultiValueMap<String, Part>.getFieldPartFile(fieldValue: String): FilePart? {
    return (this[fieldValue]?.firstOrNull() as? FilePart)
}

fun MultiValueMap<String, Part>.toMap(): Map<String, Any> {
    val result = mutableMapOf<String, Any>()
    for (entry in entries) {
        val key = entry.key
        if (entry.value.size == 1) {
            val part = entry.value[0]
            if (part.headers().contentType?.type == "image") {
//                part.inputStream.bufferedReader().use { it.readText() }
            } else {
                getFieldPartValue(part.name())?.let {
                    result.put(key, it)
                }
            }
        } else {
//            entry.value.map { part ->
//                if (part.contentType.startsWith("text")) {
//                    part.inputStream.bufferedReader().use { it.readText() }
//                } else {
//                    part.inputStream.readAllBytes()
//                }
//            }
        }
    }
    return result
}

inline fun <reified T>MultiValueMap<String, Part>.toObject(objectMapper: ObjectMapper): T {
    val convertedMap = toMap()
    return objectMapper.convertValue(convertedMap, T::class.java)
}