package com.example.pdsproject.network


import com.example.vacationhomebookingapp.HouseDeserializer
import com.example.pdsproject.model.House
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitClient {


   private const val BASE_URL_LISTING = "http://192.168.0.101:5000/"
    private const val BASE_URL_AUTHSERVICE = "http://192.168.0.101:8000/"


    val gson = GsonBuilder()
        .registerTypeAdapter(House::class.java, HouseDeserializer())
        .create()


    val instance: ListingApi by lazy {
       Retrofit.Builder()
           .baseUrl(BASE_URL_LISTING)
           .addConverterFactory(GsonConverterFactory.create(gson))
           .build()
           .create(ListingApi::class.java)
   }

    val authService: AuthService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_AUTHSERVICE)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthService::class.java)
    }
}




