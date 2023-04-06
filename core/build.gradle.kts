plugins {
    id(BuildPlugins.COMMON_SERVER)
}

dependencies {
    implementation(project(BuildModules.SERVER_COMMON))
    implementation(project(BuildModules.SERVER_LOYALTY))
    implementation(libs.bundles.serverCore)
    runtimeOnly(libs.bundles.serverCoreRuntime)
    testImplementation(libs.bundles.serverCoreTest)
    annotationProcessor(libs.bundles.serverCoreProcessor)
}
