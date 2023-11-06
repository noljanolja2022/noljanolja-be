plugins {
    id(BuildPlugins.COMMON_SERVER)
}

dependencies {
    implementation(project(BuildModules.SERVER_COMMON))
    implementation(libs.bundles.serverYoutube)
    runtimeOnly(libs.bundles.serverYoutubeRuntime)
    annotationProcessor(libs.bundles.serverGiftBizProcessor)
//    annotationProcessor(libs.bundles.serverGiftBizProcessor)
}