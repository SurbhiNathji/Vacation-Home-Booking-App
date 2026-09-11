package com.example.pdsproject.model


/**


* Description: This file defines the data model class `House` representing
* vacation home listings used throughout the application.
*
* The class implements Serializable to allow easy passing of data between
* activities. It contains fields such as title, description, price, location,
* rating, and image URL, as well as optional review information.
*
* Developed by: Surbhi Nathji


*/


import com.google.gson.annotations.SerializedName
import java.io.Serializable


/**
* Data class representing a vacation house listing.
*
* @property id Unique identifier for the house.
* @property title Title or name of the house.
* @property description A brief description of the house.
* @property price Rental price for the house.
* @property location Geographical location of the house.
* @property rating User rating out of 5.0.
* @property reviews Optional list of user review strings.
* @property imageUrl URL of the house's main image.
*/
data class House(
   val id: String,
   val title: String,
   val description: String?,
   val price:  Double, // changed to double to match the back end
   val location: String,
   val rating: Float,
   val reviews: List<Review> = emptyList(), // changed type since it's a class now
   var imageUrl: String? = null
) : Serializable  // Allows object to be passed via Intent between activities


// new class review for adding reviews
data class Review(
   @SerializedName("reviewer_id") val reviewer_id: String,
   @SerializedName("reviewer_name") val reviewerName: String,
   @SerializedName("comments") val comments: String,
   @SerializedName("date") val date: String, // Use String to control format
  // @SerializedName("token") val token: String  // Use String to control format

)
data class RegisterRequest(
   val username: String,
   val email: String,
   val password: String,
   val password_2: String,
   val first_name: String,
   val last_name: String
)

data class RegisterResponse(
   val message: String,
)

data class User(
   val email: String,
   val username: String,
   val token: String
)

data class LoginRequest(
   val email: String,
   val password: String
)

data class LoginResponse(
   val message: String,
   val tokens: Tokens,
   val user: User
)

data class Tokens(
   val refresh_token: String,
   val access_token: String
)




