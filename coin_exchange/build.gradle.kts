plugins {
    id(BuildPlugins.COMMON_SERVER)
}

dependencies {
    implementation(project(BuildModules.SERVER_COMMON))
    implementation(project(BuildModules.SERVER_LOYALTY))
    implementation(libs.bundles.serverCoinExchange)
    runtimeOnly(libs.bundles.serverCoinExchangeRuntime)
    testImplementation(libs.bundles.serverCoinExchangeTest)
//    annotationProcessor(libs.bundles.serverLoyaltyProcessor)
}