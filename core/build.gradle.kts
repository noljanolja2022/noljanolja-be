plugins {
    id(BuildPlugins.COMMON_SERVER)
}

dependencies {
    implementation(project(BuildModules.SERVER_COMMON))
    implementation(project(BuildModules.SERVER_LOYALTY))
    implementation(project(BuildModules.SERVER_COIN_EXCHANGE))
    implementation(project(BuildModules.SERVER_REWARD))
    implementation(project(BuildModules.SERVER_GIFTS))
    implementation(project(BuildModules.SERVER_YOUTUBE))
    implementation(libs.bundles.serverCore)
    runtimeOnly(libs.bundles.serverCoreRuntime)
    testImplementation(libs.bundles.serverCoreTest)
    annotationProcessor(libs.bundles.serverCoreProcessor)
}
