plugins {
    id(BuildPlugins.COMMON_SERVER)
    id(BuildPlugins.KOTLIN_PLUGIN_SERIALIZATION)
}

dependencies {
    implementation(project(BuildModules.SERVER_COMMON))
    implementation(project(BuildModules.SERVER_CORE))
    implementation("org.springframework.boot:spring-boot-starter-rsocket")
    implementation("org.springframework.security:spring-security-rsocket")
    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
    implementation(libs.bundles.serverConsumer)
    runtimeOnly(libs.bundles.serverConsumerRuntime)
    testImplementation(libs.bundles.serverConsumerTest)
    annotationProcessor(libs.bundles.serverConsumerProcessor)
}