import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id(BuildPlugins.COMMON_SERVER)
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
