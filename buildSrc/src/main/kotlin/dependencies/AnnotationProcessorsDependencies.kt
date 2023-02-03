package dependencies

/**
 * Configuration version of all annotation processors dependencies
 */

object AnnotationProcessorsDependenciesVersions

/**
 * Project annotation processors dependencies, makes it easy to include external binaries or
 * other library modules to build.
 */
object AnnotationProcessorsDependencies {
    object Server {
        const val SPRING_CONFIGURATION_PROCESSOR =
            "org.springframework.boot:spring-boot-configuration-processor"
    }
}