package com.noljanolja.server.common.util

import com.noljanolja.server.common.exception.SerializationException

inline fun <reified T : Enum<T>> enumByNameIgnoreCase(input: String, default: T? = null): T {
    return enumValues<T>().firstOrNull { it.name.equals(input, true) } ?: default
    ?: throw SerializationException("Failed to serialize value $input of type ${T::class.simpleName}")
}
