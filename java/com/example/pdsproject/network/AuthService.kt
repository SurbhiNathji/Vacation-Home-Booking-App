package com.example.pdsproject.network

import com.example.pdsproject.model.LoginRequest
import com.example.pdsproject.model.LoginResponse
import com.example.pdsproject.model.RegisterRequest
import com.example.pdsproject.model.RegisterResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST


interface AuthService {
    @POST("api/users/register/")
    fun register(@Body request: RegisterRequest): Call<RegisterResponse>

    @POST("api/users/login/")
    fun login(@Body request: LoginRequest): Call<LoginResponse>
}
