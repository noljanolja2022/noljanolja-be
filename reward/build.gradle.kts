plugins {
    id(BuildPlugins.COMMON_SERVER)
}

dependencies {
    implementation(project(BuildModules.SERVER_COMMON))
    implementation(project(BuildModules.SERVER_LOYALTY))
    implementation(libs.bundles.serverReward)
    runtimeOnly(libs.bundles.serverRewardRuntime)
    testImplementation(libs.bundles.serverRewardTest)
}
