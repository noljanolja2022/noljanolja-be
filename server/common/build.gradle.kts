import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id(BuildPlugins.COMMON_SERVER)
    id(BuildPlugins.KOTLIN_PLUGIN_SERIALIZATION)
}

dependencies {
    api(libs.bundles.serverCommon)
    runtimeOnly(libs.bundles.serverCommonRuntime)
    testImplementation(libs.bundles.serverCommonTest)
}

tasks {
    withType<BootJar> {
        enabled = false
    }
}
