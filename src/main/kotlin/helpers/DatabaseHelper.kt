package org.delcom.helpers

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database

fun Application.configureDatabases() {
    fun configValue(path: String, fallback: String): String {
        val key = when (path) {
            "ktor.database.host" -> "DB_HOST"
            "ktor.database.port" -> "DB_PORT"
            "ktor.database.name" -> "DB_NAME"
            "ktor.database.user" -> "DB_USER"
            "ktor.database.password" -> "DB_PASSWORD"
            else -> path.substringAfterLast('.').uppercase()
        }

        return environment.config.propertyOrNull(path)?.getString()
            ?: System.getProperty(key)
            ?: fallback
    }

    val dbHost = configValue("ktor.database.host", "127.0.0.1")
    val dbPort = configValue("ktor.database.port", "5432")
    val dbName = configValue("ktor.database.name", "db_pam_sukus")
    val dbUser = configValue("ktor.database.user", "postgres")
    val dbPassword = configValue("ktor.database.password", "postgres")

    Database.connect(
        url = "jdbc:postgresql://$dbHost:$dbPort/$dbName",
        user = dbUser,
        password = dbPassword
    )
}
