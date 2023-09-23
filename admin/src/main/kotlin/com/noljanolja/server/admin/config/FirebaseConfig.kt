package com.noljanolja.server.admin.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*
import javax.annotation.PostConstruct

@Configuration
class FirebaseConfig {
    private val logger = LoggerFactory.getLogger(FirebaseConfig::class.java)

    @Value("\${spring.cloud.firebase.credentials.encoded-key}")
    private val firebaseEncodedKey: String = ""

    @PostConstruct
    fun initialize() {
        try {
            val options = FirebaseOptions.builder()
                .setCredentials(
                    GoogleCredentials.fromStream(Base64.getDecoder().decode(firebaseEncodedKey).inputStream())
                )
                .build()
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                logger.info("Firebase application has been initialized")
            }
        } catch (exception: Exception) {
            logger.info("Initialize firebase failed with exception $exception")
        }
    }

    @Bean
    fun firebaseMessaging(): FirebaseMessaging = FirebaseMessaging.getInstance()
}