plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenLocal()
    google()
    mavenCentral()
}

object PluginsVersions {
    const val KOTLIN = "1.5.31"
    const val GRADLE_SPRING_BOOT = "2.5.12"
    const val GRADLE_SPRING_DM = "1.0.11.RELEASE"
    const val GRADLE_SQL_DELIGHT = "1.5.2"
    const val GRADLE_WIRE = "3.7.0"
    const val GSON = "2.8.9"
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${PluginsVersions.KOTLIN}")
    implementation("org.jetbrains.kotlin:kotlin-allopen:${PluginsVersions.KOTLIN}")
    implementation("org.jetbrains.kotlin:kotlin-serialization:${PluginsVersions.KOTLIN}")
    implementation("org.springframework.boot:spring-boot-gradle-plugin:${PluginsVersions.GRADLE_SPRING_BOOT}")
    implementation("io.spring.gradle:dependency-management-plugin:${PluginsVersions.GRADLE_SPRING_DM}")
    implementation("com.squareup.sqldelight:gradle-plugin:${PluginsVersions.GRADLE_SQL_DELIGHT}")
    implementation("com.squareup.wire:wire-gradle-plugin:${PluginsVersions.GRADLE_WIRE}")
    implementation("com.google.code.gson:gson:${PluginsVersions.GSON}")
}
