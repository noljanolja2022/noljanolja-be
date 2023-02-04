plugins {
    id(BuildPlugins.COMMON_SERVER)
}

dependencies {
    implementation(project(BuildModules.SERVER_COMMON))
    implementation(libs.bundles.serverConsumer)
    runtimeOnly(libs.bundles.serverConsumerRuntime)
    testImplementation(libs.bundles.serverConsumerTest)
    annotationProcessor(libs.bundles.serverConsumerProcessor)
}