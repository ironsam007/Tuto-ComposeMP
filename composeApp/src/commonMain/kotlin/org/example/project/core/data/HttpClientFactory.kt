package org.example.project.core.data

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging

import io.ktor.client.plugins.defaultRequest
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

import io.ktor.http.contentType

object HttpClientFactory {
    fun create(engine: HttpClientEngine): HttpClient {
        return HttpClient(engine) { //Configure client behavior as follow:
            /*
            - Add Json Ser/Deser support to the Http client
            - When parsing, extra fields in the response that my data class doesn't know about are ignored.
            */
            install(ContentNegotiation) {
                json(
                    json = Json {
                        ignoreUnknownKeys = true
                    }
                )
            }
            /*
            - Control network timeout:
            - socketTimeoutMillis: max time waiting for data exchange after a connection is established
            - request: max total time for a request to complete.
             */
            install(HttpTimeout) {
                socketTimeoutMillis = 20_000L //20s
                requestTimeoutMillis = 20_000L
            }

            /*
            - Enable Http request/response logging
            - LogLevel.All: req/res lines, headers, bodies
            - Custom logger: prints logs to the consol using println(msg)
            */
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        println(message)
                    }
                }
                level = LogLevel.ALL
            }

            /*
            sets a default content type for all requests:
            - ever request made with this clinet automatically sends this header unless i override it manually
            */
            defaultRequest {
                contentType(ContentType.Application.Json)
            }
        }
    }
}