plugins {
    id(BuildPlugins.COMMON_SERVER)
}

dependencies {
    implementation(project(BuildModules.SERVER_COMMON))
    implementation(libs.bundles.serverAdmin)
    runtimeOnly(libs.bundles.serverAdminRuntime)
    testImplementation(libs.bundles.serverAdminTest)
    annotationProcessor(libs.bundles.serverAdminProcessor)
}
