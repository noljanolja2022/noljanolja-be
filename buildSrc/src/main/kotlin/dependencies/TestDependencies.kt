package dependencies

/**
 * Configuration version of all test dependencies
 */

object TestDependenciesVersions {
    const val SPRING_MOCKK = "3.0.1"
    const val KOTLIN_COROUTINES = "1.5.2-native-mt"
    const val JUNIT = "4.13.2"
    const val OKHTTP = "4.9.0"
    const val ESPRESSO = "3.3.0"
    const val KTOR = "1.6.3"
    const val ANDROID_TEST_CORE = "1.4.0"
    const val ANDROID_TEST_EXT = "1.1.3"
    const val ROBOLECTRIC = "4.7.3"
    const val TESTCONTAINERS = "1.15.3"
    const val JIRA_TEST_JUNIT5 = "1.2.1"
}

/**
 * Project test dependencies, makes it easy to include external binaries or
 * other library modules to build.
 */
object TestDependencies {
    object Server {
        const val SPRING_TEST =
            "org.springframework.boot:spring-boot-starter-test"
        const val SPRING_MOCKK =
            "com.ninja-squad:springmockk:${TestDependenciesVersions.SPRING_MOCKK}"
        const val REACTOR_TEST =
            "io.projectreactor:reactor-test"
        const val KOTLIN_COROUTINES_TEST =
            "org.jetbrains.kotlinx:kotlinx-coroutines-test:${TestDependenciesVersions.KOTLIN_COROUTINES}"
        const val OKHTTP =
            "com.squareup.okhttp3:okhttp:${TestDependenciesVersions.OKHTTP}"
        const val MOCK_WEBSERVER =
            "com.squareup.okhttp3:mockwebserver:${TestDependenciesVersions.OKHTTP}"
        const val TESTCONTAINERS =
            "org.testcontainers:testcontainers:${TestDependenciesVersions.TESTCONTAINERS}"
        const val TESTCONTAINERS_R2DBC =
            "org.testcontainers:r2dbc:${TestDependenciesVersions.TESTCONTAINERS}"
        const val TESTCONTAINERS_MYSQL =
            "org.testcontainers:mysql:${TestDependenciesVersions.TESTCONTAINERS}"
    }

    object Domain {
        const val KOTLIN_COROUTINES =
            "org.jetbrains.kotlinx:kotlinx-coroutines-core:${TestDependenciesVersions.KOTLIN_COROUTINES}"
        const val JUNIT =
            "junit:junit:${TestDependenciesVersions.JUNIT}"
    }

    object Android {
        const val JUNIT =
            "junit:junit:${TestDependenciesVersions.JUNIT}"
        const val ESPRESSO =
            "androidx.test.espresso:espresso-core:${TestDependenciesVersions.ESPRESSO}"
    }

    object Core {
        const val KTOR_CLIENT_MOCK =
            "io.ktor:ktor-client-mock:${TestDependenciesVersions.KTOR}"

        const val ANDROID_TEST_CORE =
            "androidx.test:core:${TestDependenciesVersions.ANDROID_TEST_CORE}"
        const val ANDROID_TEST_EXT =
            "androidx.test.ext:junit:${TestDependenciesVersions.ANDROID_TEST_EXT}"
        const val ROBOLECTRIC =
            "org.robolectric:robolectric:${TestDependenciesVersions.ROBOLECTRIC}"
        const val KOTLIN_COROUTINES_TEST =
            "org.jetbrains.kotlinx:kotlinx-coroutines-test:${TestDependenciesVersions.KOTLIN_COROUTINES}"
    }
}