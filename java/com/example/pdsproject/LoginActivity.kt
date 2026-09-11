/**
 * File: LoginActivity.kt
 * Description:
 *    This activity handles user authentication by presenting a login screen.
 *    It validates user input, sends login credentials to the backend using Retrofit,
 *    and navigates to the MainActivity on successful authentication. It also provides
 *    a link to the RegisterActivity for new users.
 *
 * Developed by: Surbhi Nathji
 */

package com.example.pdsproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.ComponentActivity
import com.example.pdsproject.network.RetrofitClient
import com.example.pdsproject.model.LoginRequest
import com.example.pdsproject.model.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // UI Elements
        val email = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.password)
        val loginButton = findViewById<Button>(R.id.btnLogin)
        val registerText = findViewById<TextView>(R.id.tvRegister)

        // Login button click handler
        loginButton.setOnClickListener {

            // Get user input
            val mail = email.text.toString().trim()
            val pass = password.text.toString()

            // Validate input
            if (mail.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Email and password are required!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create a login request object
            val request = LoginRequest(email = mail, password = pass)

            // Perform the login API call
            RetrofitClient.authService.login(request)
                .enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                        if (response.isSuccessful && response.body() != null) {
                            val loginResponse = response.body()!!
                            val user = loginResponse.user
                            val accessToken=loginResponse.tokens.access_token
                            val refreshToken=loginResponse.tokens.refresh_token

                            // Save user data using SharedPreferences
                            val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                            with(sharedPreferences.edit()) {
                                putString("user_email", user.email)
                                putString("user_username", user.username)
                                putString("access_token",accessToken)
                                putString("refresh_token",refreshToken)
                                apply()
                            }

                            // Notify and navigate to main screen
                            Toast.makeText(applicationContext, "Login successful!", Toast.LENGTH_LONG).show()
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish() // Prevent returning to login screen via back button
                        } else {
                            // Handle failed response
                            val errorBody = response.errorBody()?.string()
                            Log.e("loginError", "login failed: $errorBody")
                            Toast.makeText(applicationContext, "Login failed: $errorBody", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        // Handle failure to connect or network issues
                        Toast.makeText(applicationContext, "Error: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
                    }
                })
        }

        // Navigate to the registration screen if the user doesn't have an account
        registerText.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}
