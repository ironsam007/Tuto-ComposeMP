package org.example.project.app

import kotlinx.serialization.Serializable


sealed interface Route {
    @Serializable //Todo, why serializable in Route
    data object BookGraph: Route //Bundle all nav in graph

    @Serializable
    data object BookList: Route

    @Serializable
    data class BookDetail(val id: String): Route

}
