plugins {
    id(BuildPlugins.COMMON_SERVER)
}

dependencies {
    implementation(project(BuildModules.SERVER_COMMON))
    implementation(project(BuildModules.SERVER_COIN_EXCHANGE))
    implementation(project(BuildModules.SERVER_GIFTBIZ))
    implementation(libs.bundles.serverGifts)
    runtimeOnly(libs.bundles.serverGiftsRuntime)
    testImplementation(libs.bundles.serverGiftsTest)
}