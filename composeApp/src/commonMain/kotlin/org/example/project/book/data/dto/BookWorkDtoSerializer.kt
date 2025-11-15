
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.example.project.book.data.dto.BookWorkDto
import org.example.project.book.data.dto.DescriptionDto



// Custom serializer for BookWorkDto to handle a flexible `description` field
// that may appear in JSON as either:
// 1) a string
// 2) an object { "value": "some text" }
// This serializer reads both formats and converts them into BookWorkDto(description: String?)
object BookWorkDtoSerializer: KSerializer<BookWorkDto>{

    // Defines the structure of the serialized class: one field named "description"
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor(serialName = BookWorkDto::class.simpleName!!){
        element<String?>("description") // Declares "description" as a nullable string field
    }


    // Custom deserialization logic (JSON → BookWorkDto)
    override fun deserialize(decoder: Decoder): BookWorkDto = decoder.decodeStructure(descriptor) {

        // Local variable that will hold the parsed description
        var description: String? = null

        // Loop over JSON fields as long as there are elements to decode
        while(true){
            when(val index = decodeElementIndex(descriptor)){ //index from descriptor order

                // Field index 0 → corresponds to "description"
                0 -> {
                    // Ensure that we are decoding JSON, otherwise fail
                    val jsonDecoder = decoder as? JsonDecoder ?: throw SerializationException(
                        "this decoder only works with JSON"
                    )

                    // Read the raw JSON element associated with this field
                    val element  = jsonDecoder.decodeJsonElement()

                    // Case 1: description is an object → decode DescriptionDto then extract its value
                    description = if(element is JsonObject){
                        decoder.json.decodeFromJsonElement<DescriptionDto>(
                            element = element,
                            deserializer = DescriptionDto.serializer()
                        ).value

                    // Case 2: description is a string → use it directly
                    } else  if(element is JsonPrimitive && element.isString){
                        element.content

                    // Case 3: unsupported type → treat as null
                    }else null
                }

                //No more fields to decode
                CompositeDecoder.DECODE_DONE -> break

                //any unexpected field index -> error
                else -> throw SerializationException("Unexpected index $index")
            }
        }
        //construct and return the final data object
        return@decodeStructure BookWorkDto(description)
    }

    // Custom serialization logic (BookWorkDto → JSON)
    override fun serialize(encoder: Encoder, value: BookWorkDto) = encoder.encodeStructure(descriptor){

        //only write out the description if it is non null
        value.description?.let{
            encodeStringElement(descriptor, 0, it)
        }
    }
}