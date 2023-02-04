plugins {
    id(BuildPlugins.COMMON_SERVER)
}

dependencies {
    implementation(project(BuildModules.SERVER_COMMON))
    implementation(libs.bundles.serverAuth)
    runtimeOnly(libs.bundles.serverAuthRuntime)
    testImplementation(libs.bundles.serverAuthTest)
    annotationProcessor(libs.bundles.serverAuthProcessor)
}