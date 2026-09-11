package com.example.vacationhomebookingapp

/**
 * File: HouseDeserializer.kt
 * Description: Custom Gson deserializer for the House model.
 *              Handles JSON deserialization to convert JSON data into House objects,
 *              including optional fields and handling of different JSON formats for reviews.
 *
 * Developed by: Surbhi Nathji
 */

import com.example.pdsproject.model.House
import com.example.pdsproject.model.Review
import com.google.gson.*
import java.lang.reflect.Type

/**
 * Deserializes JSON into a House object, handling optional fields and
 * potential variations in the "reviews" JSON structure.
 */
class HouseDeserializer : JsonDeserializer<House> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): House {
        val jsonObject = json.asJsonObject

        // Extract mandatory and optional fields from JSON, with safe null checks
        val id = jsonObject.get("id").asString
        val title = jsonObject.get("title").asString
        val description = if (jsonObject.has("description") && !jsonObject.get("description").isJsonNull)
            jsonObject.get("description").asString else ""
        val price = jsonObject.get("price").asDouble
        val location = jsonObject.get("location").asString
        val rating = if (jsonObject.has("rating") && !jsonObject.get("rating").isJsonNull)
            jsonObject.get("rating").asFloat else 0f
        val imageUrl = if (jsonObject.has("imageUrl") && !jsonObject.get("imageUrl").isJsonNull)
            jsonObject.get("imageUrl").asString else null

        // Handle the reviews field, which might be a JsonArray or a string ("No reviews")
        val reviews = if (jsonObject.has("reviews")) {
            val reviewsElement = jsonObject.get("reviews")
            if (reviewsElement.isJsonArray) {
                val jsonArray = reviewsElement.asJsonArray
                // Deserialize each review element safely, ignoring malformed entries
                jsonArray.mapNotNull { element ->
                    try {
                        context.deserialize<Review>(element, Review::class.java)
                    } catch (e: Exception) {
                        null
                    }
                }
            } else {
                // If reviews is not an array (e.g., a string), return empty list
                emptyList()
            }
        } else {
            // No reviews field present
            emptyList()
        }

        // Construct and return the House object
        return House(
            id = id,
            title = title,
            description = description,
            price = price,
            location = location,
            rating = rating,
            reviews = reviews,
            imageUrl = imageUrl
        )
    }
}
