plugins {
    id(BuildPlugins.COMMON_SERVER)
    id(BuildPlugins.KOTLIN_PLUGIN_SERIALIZATION)
}

dependencies {
    implementation(project(BuildModules.SERVER_COMMON))
    implementation(libs.bundles.serverConsumer)
    runtimeOnly(libs.bundles.serverConsumerRuntime)
    testImplementation(libs.bundles.serverConsumerTest)
    annotationProcessor(libs.bundles.serverConsumerProcessor)
}