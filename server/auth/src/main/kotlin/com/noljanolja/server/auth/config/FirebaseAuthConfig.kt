package com.noljanolja.server.auth.config

import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FirebaseAuthConfig {

    @Bean
    fun firebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance(FirebaseApp.initializeApp())
    }
}