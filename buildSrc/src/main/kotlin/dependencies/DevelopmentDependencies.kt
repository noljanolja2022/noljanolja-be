package dependencies

/**
 * Configuration version of all development dependencies
 */

object DevelopmentDependenciesVersions {

}

/**
 * Project development dependencies, makes it easy to include external binaries or
 * other library modules to build.
 */
object DevelopmentDependencies {
    object Server {
        const val SPRING_DEVTOOLS = "org.springframework.boot:spring-boot-devtools"
    }
}