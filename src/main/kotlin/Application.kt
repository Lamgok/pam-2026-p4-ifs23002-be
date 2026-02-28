package org.delcom

import io.github.cdimascio.dotenv.dotenv
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import java.io.File
import kotlinx.serialization.json.Json
import org.delcom.helpers.configureDatabases
import org.delcom.module.appModule
import org.koin.ktor.plugin.Koin

fun main(args: Array<String>) {
    val envDirectory = System.getProperty("dotenv.dir")
        ?: System.getenv("DOTENV_DIR")
        ?: File(System.getProperty("user.dir")).absolutePath

    val dotenv = dotenv {
        directory = envDirectory
        ignoreIfMissing = true
    }

    dotenv.entries().forEach {
        if (System.getProperty(it.key).isNullOrBlank()) {
            System.setProperty(it.key, it.value)
        }
    }

    EngineMain.main(args)
}

fun Application.module() {

    install(CORS) {
        anyHost()
    }

    install(ContentNegotiation) {
        json(
            Json {
                explicitNulls = false
                prettyPrint = true
                ignoreUnknownKeys = true
            }
        )
    }

    install(Koin) {
        modules(appModule)
    }

    configureDatabases()
    configureRouting()
}
