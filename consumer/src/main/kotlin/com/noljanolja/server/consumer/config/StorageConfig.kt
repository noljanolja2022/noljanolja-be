package com.noljanolja.server.consumer.config

import com.google.auth.oauth2.ServiceAccountCredentials
import com.google.cloud.storage.Storage
import com.google.cloud.storage.StorageOptions
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*

@Configuration
class StorageConfig {
    companion object {
        private const val PROJECT_ID = "nolajnolja"
    }
    @Value("\${spring.storage.google.credentials.encoded-key}")
    private val gcpEncodedKey: String = ""

    @Bean
    fun cloudStorage(
        credentials: ServiceAccountCredentials
    ): Storage {
        return StorageOptions.newBuilder()
            .setProjectId(PROJECT_ID)
            .setCredentials(credentials)
            .build()
            .service
    }

    @Bean
    fun serviceAccounts(): ServiceAccountCredentials {
        return ServiceAccountCredentials.fromStream(Base64.getDecoder().decode(gcpEncodedKey).inputStream())
    }
}