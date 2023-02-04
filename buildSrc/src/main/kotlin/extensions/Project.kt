package extensions

import org.gradle.api.Project

/**
 * Obtain property declared on `$projectRoot/local.properties` file.
 *
 * @param propertyName the name of declared property
 */
fun Project.getLocalProperty(propertyName: String): String =
    utils.getLocalProperty(this, propertyName)