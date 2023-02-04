package utils

import org.gradle.api.Project
import java.util.*

private const val LOCAL_PROPERTIES_FILE_NAME = "local.properties"

/**
 * Util to obtain property declared on `$projectRoot/local.properties` file.
 *
 * @param project the project reference
 * @param propertyName the name of declared property
 *
 * @return the value of property name, otherwise throw [Exception]
 */
fun getLocalProperty(project: Project, propertyName: String): String {
    return getPropertyFromFile(project, LOCAL_PROPERTIES_FILE_NAME, propertyName)
}

fun getPropertyFromFile(project: Project, fileName: String, propertyName: String): String {
    // We will try to get the value from env first because it can be overridden
    System.getenv(propertyName)?.let { propertyValue ->
        return propertyValue
    }

    val properties = Properties().apply {
        val propertiesFile = project.rootProject.file(fileName)
        if (propertiesFile.exists()) {
            load(propertiesFile.inputStream())
        }
    }

    return properties.getProperty(propertyName) ?: throw NoSuchFieldException("Not defined property: $propertyName")
}