plugins {
    id(BuildPlugins.KOTLIN_PLUGIN_SERIALIZATION)
    id(BuildPlugins.COMMON_SERVER)
}

dependencies {
    implementation(project(BuildModules.SERVER_COMMON))
    implementation(libs.bundles.serverCore)
    runtimeOnly(libs.bundles.serverCoreRuntime)
    testImplementation(libs.bundles.serverCoreTest)
    annotationProcessor(libs.bundles.serverCoreProcessor)
}