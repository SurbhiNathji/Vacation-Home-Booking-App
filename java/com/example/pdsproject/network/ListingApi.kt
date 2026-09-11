package com.example.pdsproject.network




import com.example.pdsproject.model.House
import com.example.pdsproject.model.Review
import retrofit2.Call
import retrofit2.http.*
import okhttp3.ResponseBody


interface ListingApi {
   // 1. Get list of house summaries
   @GET("api/listings")
   fun getHouseSummaries(
      @Header("Authorization") authHeader: String,
      ): Call<List<House>>




   // 2. Get full house details by ID
   @GET("api/listings/{id}")
   fun getHouseDetails(
      @Header("Authorization") authHeader: String,
      @Path("id") id: String
   ): Call<House>


   // 3. Add a review to a house
   @POST("api/listings/{id}/reviews")
   fun addReview(
      @Header("Authorization") authHeader: String,
       @Path("id") houseId: String,
       @Body review: Review
   ): Call<ResponseBody>
}




