plugins {
    id(BuildPlugins.COMMON_SERVER)
    id(BuildPlugins.KOTLIN_PLUGIN_SERIALIZATION)
}

dependencies {
    implementation(project(BuildModules.SERVER_COMMON))
    implementation(project(BuildModules.SERVER_CORE))
    implementation(libs.bundles.serverAuth)
    runtimeOnly(libs.bundles.serverAuthRuntime)
    testImplementation(libs.bundles.serverAuthTest)
    annotationProcessor(libs.bundles.serverAuthProcessor)
}