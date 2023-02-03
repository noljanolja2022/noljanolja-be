import dependencies.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id(BuildPlugins.SPRING_BOOT)
    id(BuildPlugins.SPRING_DM)
    id(BuildPlugins.KOTLIN_JVM)
    id(BuildPlugins.KOTLIN_PLUGIN_SPRING)
    id(BuildPlugins.KOTLIN_PLUGIN_SERIALIZATION)
    id(BuildPlugins.KOTLIN_KAPT)
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2020.0.4")
        mavenBom("org.apache.logging.log4j:log4j-bom:2.16.0")
    }
}

kotlin {
    java {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(Dependencies.Server.SPRING_WEBFLUX) {
        exclude("org.springframework.boot", "spring-boot-starter-json")
    }
    implementation(Dependencies.Server.SPRING_DATA_R2DBC)
    implementation(Dependencies.Server.SPRING_DATA_REDIS_REACTIVE)
    implementation(Dependencies.Server.GOOGLE_PROTOBUF_JAVA)
    implementation(Dependencies.Server.KOTLIN)
    implementation(Dependencies.Server.KOTLIN_REFLECT)
    implementation(Dependencies.Server.KOTLIN_COROUTINES)
    implementation(Dependencies.Server.KOTLIN_COROUTINES_REACTOR)
    implementation(Dependencies.Server.KOTLIN_COROUTINES_JDK8)
    implementation(Dependencies.Server.KOTLIN_SERIALIZATION)
    implementation(Dependencies.Server.KOTLIN_DATE_TIME)
    implementation(Dependencies.Server.REACTOR_KOTLIN)

    implementation(Dependencies.Server.SPRING_BOOT_STARTER_ACTUATOR)
    implementation(Dependencies.Server.MICROMETER_REGISTRY_PROMETHEUS)

    implementation(Dependencies.Server.JWT)

    implementation(Dependencies.Server.WIRE_CLIENT)
    implementation(Dependencies.Server.WIRE_KOTLIN_SERIALIZATION)

    implementation(Dependencies.Server.SPRING_CLOUD_SLEUTH)
    implementation(Dependencies.Server.SPRING_CLOUD_SLEUTH_ZIPKIN)
    implementation(Dependencies.Server.OPENTRACING_BRAVE)

    kapt(AnnotationProcessorsDependencies.Server.SPRING_CONFIGURATION_PROCESSOR)

    developmentOnly(DevelopmentDependencies.Server.SPRING_DEVTOOLS)

    runtimeOnly(RuntimeDependencies.Server.MYSQL)
    runtimeOnly(RuntimeDependencies.Server.R2DBC_MYSQL)

    testImplementation(TestDependencies.Server.SPRING_TEST) {
        exclude(module = "junit")
        exclude(module = "mockito-core")
    }
    testImplementation(TestDependencies.Server.REACTOR_TEST)
    testImplementation(TestDependencies.Server.KOTLIN_COROUTINES_TEST)
    testImplementation(TestDependencies.Server.SPRING_MOCKK)
    testImplementation(TestDependencies.Server.OKHTTP)
    testImplementation(TestDependencies.Server.MOCK_WEBSERVER)
    testImplementation(TestDependencies.Server.TESTCONTAINERS)
    testImplementation(TestDependencies.Server.TESTCONTAINERS_R2DBC)
    testImplementation(TestDependencies.Server.TESTCONTAINERS_MYSQL)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = JavaVersion.VERSION_11.toString()
    }
}