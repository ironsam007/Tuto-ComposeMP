package org.example.project.book.data.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer

/*
looking into the api response, in some end point, description is astring in other it is a json object.
    => This could be because the api was built for JS web application known for easy parsing. compared to
        mobile dev that works mostly with type safety when parsing json data.
    => To work around this, we need to customize serialization/deserialization
*/


@Serializable(with = BookWorkDtoSerializer::class) //custom serialization with BookWorkDtoSerializer
data class BookWorkDto(
    val description: String? = null
)