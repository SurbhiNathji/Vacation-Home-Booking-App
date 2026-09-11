/**
 * File: RegisterActivity.kt
 * Description:
 *    Handles the user registration screen for the Vacation Home Booking App.
 *    Collects user details and performs a registration API call using Retrofit.
 *    Navigates to Login screen on successful registration.
 *
 * Features:
 *    - Input validation for required fields
 *    - Password match verification
 *    - Sends user data to backend via Retrofit
 *    - Handles API response and displays feedback
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
import com.example.pdsproject.model.RegisterRequest
import com.example.pdsproject.model.RegisterResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Bind UI elements
        val username = findViewById<EditText>(R.id.username)
        val email = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.password)
        val password2 = findViewById<EditText>(R.id.password2)
        val firstName = findViewById<EditText>(R.id.firstname)
        val lastName = findViewById<EditText>(R.id.lastname)
        val registerButton = findViewById<Button>(R.id.btnRegister)

        // Handle register button click
        registerButton.setOnClickListener {
            // Collect input values
            val user = username.text.toString().trim()
            val mail = email.text.toString().trim()
            val pass = password.text.toString()
            val pass2 = password2.text.toString()
            val first = firstName.text.toString().trim()
            val last = lastName.text.toString().trim()

            // Validate all fields
            if (user.isEmpty() || mail.isEmpty() || pass.isEmpty() || pass2.isEmpty() || first.isEmpty() || last.isEmpty()) {
                Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check if passwords match
            if (pass != pass2) {
                Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Prepare request body for API call
            val request = RegisterRequest(
                username = user,
                email = mail,
                password = pass,
                password_2 = pass2,
                first_name = first,
                last_name = last
            )

            // Make registration API call via Retrofit
            RetrofitClient.authService.register(request).enqueue(object : Callback<RegisterResponse> {
                override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        Toast.makeText(applicationContext, "Registration successful!", Toast.LENGTH_LONG).show()

                        // Redirect to LoginActivity
                        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                        finish()
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("RegistrationError", "Registration failed: $errorBody")
                        Toast.makeText(applicationContext, "Registration failed: $errorBody", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    Toast.makeText(applicationContext, "Error: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            })
        }
    }
}
