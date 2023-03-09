plugins {
    id(BuildPlugins.COMMON_SERVER)
    kotlin(BuildPlugins.KOTLIN_SERIALIZATION) version "1.8.10"
}

dependencies {
    implementation(project(BuildModules.SERVER_COMMON))
    implementation(libs.bundles.serverConsumer)
    runtimeOnly(libs.bundles.serverConsumerRuntime)
    testImplementation(libs.bundles.serverConsumerTest)
    annotationProcessor(libs.bundles.serverConsumerProcessor)
}