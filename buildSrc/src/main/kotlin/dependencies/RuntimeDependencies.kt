package dependencies

/**
 * Configuration version of all runtime dependencies
 */

object RuntimeDependenciesVersions {

}

/**
 * Project runtime dependencies, makes it easy to include external binaries or
 * other library modules to build.
 */
object RuntimeDependencies {
    object Server {
        const val R2DBC_MYSQL = "dev.miku:r2dbc-mysql"
        const val R2DBC_H2 = "io.r2dbc:r2dbc-h2"
        const val MYSQL = "mysql:mysql-connector-java"
        const val H2 = "com.h2database:h2"
    }
}