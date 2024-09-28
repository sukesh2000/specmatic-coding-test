package com.store.serialization

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer

class StringOnlyDeserializer : JsonDeserializer<String>() {
    override fun deserialize(parser: JsonParser, context: DeserializationContext): String {
        val node = parser.codec.readTree<com.fasterxml.jackson.databind.JsonNode>(parser)
        if (!node.isTextual) {
            throw IllegalArgumentException("Name must be a string")
        }
        return node.asText()
    }
}
