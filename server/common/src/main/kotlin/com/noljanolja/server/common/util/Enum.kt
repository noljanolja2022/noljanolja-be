package com.noljanolja.server.common.util

inline fun <reified T : Enum<T>> enumByNameIgnoreCase(input: String, default: T): T {
    return enumValues<T>().firstOrNull { it.name.equals(input, true) } ?: default
}
