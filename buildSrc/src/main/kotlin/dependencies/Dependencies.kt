package dependencies

/**
 * Configuration version of all dependencies
 */

object DependenciesVersions {
    const val KOTLIN = "1.5.31"
    const val KOTLIN_COROUTINES = "1.5.2-native-mt"
    const val KOTLIN_SERIALIZATION = "1.3.0"
    const val KOTLIN_DATE_TIME = "0.3.1"
    const val KOTLIN_META_DATA = "0.2.0"
    const val JWT = "3.11.0"
    const val MICROMETER_REGISTRY_PROMETHEUS = "1.5.1"
    const val WIRE = "3.7.0"
    const val OKHTTP = "4.9.0"
    const val GOOGLE_PROTOBUF_JAVA = "3.21.5"
    const val KTOR = "1.6.3"
    const val KODEIN_DI = "7.9.0"
    const val LOGGER = "1.2.3"
    const val SQL_DELIGHT = "1.5.2"
    const val FAST_ADAPTER = "5.3.5"
    const val COIL = "2.0.0-alpha06"
    const val DAGGER_COMPILER = "2.26"
    const val LIFECYCLE = "2.4.0"
    const val NAVIGATION = "2.4.0-rc01"
    const val FLOW_BINDING = "1.0.0"
    const val STATELY_COMMON = "1.1.7"
    const val STATELY_CONCURRENCY = "1.1.7"
    const val NAPIER_LOGGER = "2.1.0"
    const val TIMBER = "5.0.1"
    const val JETPACK_COMPOSE = "1.0.4"
    const val COMPOSE_MATERIAL3 = "1.0.0-alpha06"
    const val CONSTRAIN_LAYOUT_COMPOSE = "1.0.0-rc01"
    const val APPCOMPAT = "1.3.1"
    const val CORE_DESUGAR = "1.1.5"
    const val FLOW_LAYOUT = "0.20.0"
    const val REFRESH_LAYOUT = "1.1.0"
    const val COMPOSE_REFRESH_LAYOUT = "0.24.3-alpha"

    // Teko
    const val TERRA_CORE = "0.8.0"
    const val APOLLO = "2.5.0"
    const val TERRA_APOLLO = "2.1.0"
    const val DISCOVERY = "0.0.10"
    const val AUTH = "0.6.0-alpha1"
    const val TERRA_LOYALTY = "3.0.0"
    const val LOYALTY_COMPONENT = "3.1.0"
    const val TERRA_TRACKER = "2.2.0"
    const val PAYMENT_UI = "3.1.0-alpha.1-SNAPSHOT"
    const val PAYMENT_SELECT_METHODS_UI = "0.0.8-alpha.1"
    const val TERRA_USER = "0.4.24"
    const val TERRA_USER_ANDROID = "0.4.20"

    //React native
    const val REACT_NATIVE = "0.63.4"

    // Trace leak memory
    const val LEAK_CANARY = "2.9.1"
}

/**
 * Project dependencies, makes it easy to include external binaries or
 * other library modules to build.
 */
object Dependencies {
    object Server {
        const val KOTLIN =
            "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${DependenciesVersions.KOTLIN}"
        const val KOTLIN_REFLECT =
            "org.jetbrains.kotlin:kotlin-reflect:${DependenciesVersions.KOTLIN}"
        const val KOTLIN_COROUTINES =
            "org.jetbrains.kotlinx:kotlinx-coroutines-core:${DependenciesVersions.KOTLIN_COROUTINES}"
        const val KOTLIN_COROUTINES_REACTOR =
            "org.jetbrains.kotlinx:kotlinx-coroutines-reactor:${DependenciesVersions.KOTLIN_COROUTINES}"
        const val KOTLIN_COROUTINES_JDK8 =
            "org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:${DependenciesVersions.KOTLIN_COROUTINES}"
        const val KOTLIN_SERIALIZATION =
            "org.jetbrains.kotlinx:kotlinx-serialization-json:${DependenciesVersions.KOTLIN_SERIALIZATION}"
        const val KOTLIN_DATE_TIME =
            "org.jetbrains.kotlinx:kotlinx-datetime:${DependenciesVersions.KOTLIN_DATE_TIME}"

        const val SPRING_WEBFLUX =
            "org.springframework.boot:spring-boot-starter-webflux"
        const val SPRING_BOOT_STARTER_ACTUATOR =
            "org.springframework.boot:spring-boot-starter-actuator"
        const val MICROMETER_REGISTRY_PROMETHEUS =
            "io.micrometer:micrometer-registry-prometheus:${DependenciesVersions.MICROMETER_REGISTRY_PROMETHEUS}"

        const val SPRING_DATA_R2DBC =
            "org.springframework.boot:spring-boot-starter-data-r2dbc"
        const val SPRING_DATA_REDIS_REACTIVE =
            "org.springframework.boot:spring-boot-starter-data-redis-reactive"

        const val SPRING_CLOUD_SLEUTH =
            "org.springframework.cloud:spring-cloud-starter-sleuth"
        const val SPRING_CLOUD_SLEUTH_ZIPKIN =
            "org.springframework.cloud:spring-cloud-sleuth-zipkin"
        const val OPENTRACING_BRAVE =
            "io.opentracing.brave:brave-opentracing"

        const val REACTOR_KOTLIN =
            "io.projectreactor.kotlin:reactor-kotlin-extensions"

        const val JWT =
            "com.auth0:java-jwt:${DependenciesVersions.JWT}"

        const val WIRE_CLIENT =
            "com.squareup.wire:wire-grpc-client:${DependenciesVersions.WIRE}"
        const val WIRE_KOTLIN_SERIALIZATION =
            "com.squareup.wire:wire-kotlin-serialization:${DependenciesVersions.WIRE}"
        const val OKHTTP =
            "com.squareup.okhttp3:okhttp:${DependenciesVersions.OKHTTP}"

        const val GOOGLE_PROTOBUF_JAVA =
            "com.google.protobuf:protobuf-java:${DependenciesVersions.GOOGLE_PROTOBUF_JAVA}"
    }

    object Domain {
        const val KOTLIN_COROUTINES =
            "org.jetbrains.kotlinx:kotlinx-coroutines-core:${DependenciesVersions.KOTLIN_COROUTINES}"
        const val KOTLIN_SERIALIZATION =
            "org.jetbrains.kotlinx:kotlinx-serialization-json:${DependenciesVersions.KOTLIN_SERIALIZATION}"
        const val KOTLIN_DATE_TIME =
            "org.jetbrains.kotlinx:kotlinx-datetime:${DependenciesVersions.KOTLIN_DATE_TIME}"
    }

    object Core {
        const val KTOR_CLIENT =
            "io.ktor:ktor-client-core:${DependenciesVersions.KTOR}"
        const val KTOR_CLIENT_JSON =
            "io.ktor:ktor-client-json:${DependenciesVersions.KTOR}"
        const val KTOR_CLIENT_LOGGING =
            "io.ktor:ktor-client-logging:${DependenciesVersions.KTOR}"
        const val KTOR_CLIENT_SERIALIZATION =
            "io.ktor:ktor-client-serialization:${DependenciesVersions.KTOR}"
        const val KTOR_CLIENT_OKHTTP =
            "io.ktor:ktor-client-okhttp:${DependenciesVersions.KTOR}"
        const val KTOR_CLIENT_IOS =
            "io.ktor:ktor-client-ios:${DependenciesVersions.KTOR}"

        const val KODEIN_DI =
            "org.kodein.di:kodein-di:${DependenciesVersions.KODEIN_DI}"
        const val KODEIN_CONFIGURABLE =
            "org.kodein.di:kodein-di-conf:${DependenciesVersions.KODEIN_DI}"

        const val LOGGER =
            "ch.qos.logback:logback-classic:${DependenciesVersions.LOGGER}"

        const val SQL_DELIGHT =
            "com.squareup.sqldelight:runtime:${DependenciesVersions.SQL_DELIGHT}"
        const val SQL_DELIGHT_ANDROID =
            "com.squareup.sqldelight:android-driver:${DependenciesVersions.SQL_DELIGHT}"
        const val SQL_DELIGHT_IOS =
            "com.squareup.sqldelight:native-driver:${DependenciesVersions.SQL_DELIGHT}"
        const val SQL_DELIGHT_COROUTINE =
            "com.squareup.sqldelight:coroutines-extensions:${DependenciesVersions.SQL_DELIGHT}"

        const val STATELY_COMMON = "co.touchlab:stately-common:${DependenciesVersions.STATELY_COMMON}"
        const val STATELY_CONCURRENCY = "co.touchlab:stately-concurrency:${DependenciesVersions.STATELY_CONCURRENCY}"

        const val NAPIER_LOGGER = "io.github.aakira:napier:${DependenciesVersions.NAPIER_LOGGER}"
    }

    object Android {
        // Flow layout
        const val FLOW_LAYOUT = "com.google.accompanist:accompanist-flowlayout:${DependenciesVersions.FLOW_LAYOUT}"

        //Desugar
        const val CORE_DESUGAR = "com.android.tools:desugar_jdk_libs:${DependenciesVersions.CORE_DESUGAR}"

        const val APPCOMPAT = "androidx.appcompat:appcompat:${DependenciesVersions.APPCOMPAT}"
        const val FAST_ADAPTER =
            "com.mikepenz:fastadapter:${DependenciesVersions.FAST_ADAPTER}"
        const val FAST_ADAPTER_BINDING =
            "com.mikepenz:fastadapter-extensions-binding:${DependenciesVersions.FAST_ADAPTER}"
        const val FAST_ADAPTER_DIFF =
            "com.mikepenz:fastadapter-extensions-diff:${DependenciesVersions.FAST_ADAPTER}"
        const val FAST_ADAPTER_EXT =
            "com.mikepenz:fastadapter-extensions-ui:${DependenciesVersions.FAST_ADAPTER}"

        // refresh layout
        const val REFRESH_LAYOUT =
            "androidx.swiperefreshlayout:swiperefreshlayout:${DependenciesVersions.REFRESH_LAYOUT}"
        const val COMPOSE_REFRESH_LAYOUT =
            "com.google.accompanist:accompanist-swiperefresh:${DependenciesVersions.COMPOSE_REFRESH_LAYOUT}"

        // coil
        const val COIL = "io.coil-kt:coil:${DependenciesVersions.COIL}"
        const val COIL_COMPOSE = "io.coil-kt:coil-compose:${DependenciesVersions.COIL}"

        // TODO : reuse Kodein
        const val DAGGER_COMPILER =
            "com.google.dagger:dagger-compiler:${DependenciesVersions.DAGGER_COMPILER}"
        const val DAGGER_PROCESSOR =
            "com.google.dagger:dagger-android-processor:${DependenciesVersions.DAGGER_COMPILER}"

        // lifecycle
        const val LIFECYCLE_VIEWMODEL =
            "androidx.lifecycle:lifecycle-viewmodel-ktx:${DependenciesVersions.LIFECYCLE}"
        const val LIFECYCLE_KTX =
            "androidx.lifecycle:lifecycle-runtime-ktx:${DependenciesVersions.LIFECYCLE}"

        const val NAVIGATION_FRAGMENT_KTX =
            "androidx.navigation:navigation-fragment-ktx:${DependenciesVersions.NAVIGATION}"

        const val TIMBER = "com.jakewharton.timber:timber:${DependenciesVersions.TIMBER}"

        // Flow Binding
        const val FLOW_BINDING_ANDROID =
            "io.github.reactivecircus.flowbinding:flowbinding-android:${DependenciesVersions.FLOW_BINDING}"

        // Jetpack compose
        const val COMPOSE_UI = "androidx.compose.ui:ui:${DependenciesVersions.JETPACK_COMPOSE}"
        const val COMPOSE_UI_TOOL = "androidx.compose.ui:ui-tooling:${DependenciesVersions.JETPACK_COMPOSE}"
        const val COMPOSE_FOUNDATION = "androidx.compose.foundation:foundation:${DependenciesVersions.JETPACK_COMPOSE}"
        const val COMPOSE_MATERIAL = "androidx.compose.material:material:${DependenciesVersions.JETPACK_COMPOSE}"
        const val CONSTRAIN_LAYOUT_COMPOSE =
            "androidx.constraintlayout:constraintlayout-compose:${DependenciesVersions.CONSTRAIN_LAYOUT_COMPOSE}"
        const val COMPOSE_MATERIAL3 =
            "androidx.compose.material3:material3:${DependenciesVersions.COMPOSE_MATERIAL3}"

        // Teko
        const val TERRA_CORE =
            "vn.teko.terra:terra-core-android:${DependenciesVersions.TERRA_CORE}"
        const val APOLLO =
            "vn.teko.apollo:apollo:${DependenciesVersions.APOLLO}"
        const val TERRA_APOLLO =
            "vn.teko.apollo:terra-apollo:${DependenciesVersions.TERRA_APOLLO}"
        const val TERRA_TRACKER =
            "vn.teko.android.tracker:tracker-manager:${DependenciesVersions.TERRA_TRACKER}"
        const val DISCOVERY_EVENT = "vn.teko.discovery:discovery-event:${DependenciesVersions.DISCOVERY}"
        const val AUTH_LOGIN_UI = "vn.teko.android.auth:login-ui:${DependenciesVersions.AUTH}"
        const val TERRA_AUTH = "vn.teko.android.auth:terra-auth:${DependenciesVersions.AUTH}"
        const val TERRA_LOYALTY = "vn.teko.loyalty:terra-loyalty:${DependenciesVersions.TERRA_LOYALTY}"
        const val LOYALTY_COMPONENT = "vn.teko.loyalty:loyalty-component:${DependenciesVersions.LOYALTY_COMPONENT}"
        const val PAYMENT_UI = "vn.teko.android.payment:payment-ui:${DependenciesVersions.PAYMENT_UI}"
        const val PAYMENT_SELECT_METHODS_UI =
            "vn.teko.android.payment:payment-select-methods-ui:${DependenciesVersions.PAYMENT_SELECT_METHODS_UI}"

        const val KOTLIN_META_DATA =
            "org.jetbrains.kotlinx:kotlinx-metadata-jvm:${dependencies.DependenciesVersions.KOTLIN_META_DATA}"

        const val TERRA_HESTIA = "vn.teko.hestia:terra-hestia:+"
        const val HESTIA_ANDROID_REACT_NATIVE_UI_FRAGMENT = "vn.teko.hestia:hestia-android-react-native-ui-fragment:+"

        const val TERRA_USER_ANDROID = "vn.teko.terra:service-user-android:${dependencies.DependenciesVersions.TERRA_USER_ANDROID}"
        const val TERRA_USER = "vn.teko.terra:service-user:${dependencies.DependenciesVersions.TERRA_USER}"
        const val JSC_FLAVOR = "org.webkit:android-jsc:+"
        const val REACT_NATIVE = "com.facebook.react:react-native:${dependencies.DependenciesVersions.REACT_NATIVE}"

        // Trace leak memory
        const val LEAK_CANARY = "com.squareup.leakcanary:leakcanary-android:${DependenciesVersions.LEAK_CANARY}"
    }

    object Display {
        const val KOTLIN_SERIALIZATION =
            "org.jetbrains.kotlinx:kotlinx-serialization-json:${DependenciesVersions.KOTLIN_SERIALIZATION}"
        const val KOTLIN_DATE_TIME =
            "org.jetbrains.kotlinx:kotlinx-datetime:${DependenciesVersions.KOTLIN_DATE_TIME}"
    }
}