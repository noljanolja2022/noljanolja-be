import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id(BuildPlugins.COMMON_SERVER)
}

dependencies {
    implementation(project(BuildModules.SERVER_COMMON))
    implementation(libs.bundles.serverLoyalty)
    runtimeOnly(libs.bundles.serverLoyaltyRuntime)
    testImplementation(libs.bundles.serverLoyaltyTest)
//    annotationProcessor(libs.bundles.serverLoyaltyProcessor)
}

tasks {
    withType<BootJar> {
        enabled = false
    }
}