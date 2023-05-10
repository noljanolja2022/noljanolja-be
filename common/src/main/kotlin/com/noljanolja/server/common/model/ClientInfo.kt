package com.noljanolja.server.common.model

data class ClientInfo(
    val appVersion: String,
    val type: String,
    val platformName: String,
    val platformVersion: String,
    val deviceName: String,
) {
    companion object {
        private val regex = Regex("^(\\w+\\/\\d+\\.\\d+\\.\\d+) \\((\\w+); (\\w+) (\\d+); (.+)\\)\$")
        fun parseString(userAgent: String): ClientInfo? = regex.find(userAgent)?.let {
            val (appVersion, type, platformName, platformVersion, deviceName) = it.destructured
            ClientInfo(
                appVersion = appVersion,
                type = type.uppercase(),
                platformName = platformName,
                platformVersion = platformVersion,
                deviceName = deviceName,
            )
        }
    }
}