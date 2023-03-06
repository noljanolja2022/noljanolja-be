plugins {
    `kotlin-dsl`
}

repositories {
    mavenLocal()
    gradlePluginPortal()
    google()
    mavenCentral()
}

dependencies {
    implementation(libs.bundles.gradlePlugins)
}
