package com.noljanolja.server.auth.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.Base64

@Configuration
class FirebaseAuthConfig {
    companion object {
        const val SERVICE_ACCOUNT_CREDENTIALS = "SERVICE_ACCOUNT_CREDENTIALS"
    }

    @Bean
    fun firebaseAuth(): FirebaseAuth {
        val options = FirebaseOptions.builder()
            .setCredentials(
                GoogleCredentials.fromStream(
                    Base64.getDecoder().decode(System.getenv(SERVICE_ACCOUNT_CREDENTIALS)).inputStream()
                )
            )
            .build()
        return FirebaseAuth.getInstance(FirebaseApp.initializeApp(options))
    }
}