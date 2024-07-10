package de.evylon.shoppinglist.models

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject

object ItemSerializer : JsonContentPolymorphicSerializer<Item>(Item::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<Item> {
        val jsonObject = element.jsonObject
        return when {
            jsonObject.containsKey("stringRepresentation") -> Item.Text.serializer()
            jsonObject.containsKey("name") -> Item.Data.serializer()
            else -> throw IllegalArgumentException("Unsupported Base type.")
        }
    }
}
