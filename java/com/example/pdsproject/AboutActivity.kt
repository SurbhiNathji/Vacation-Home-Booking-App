package com.example.pdsproject

import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity

/**
 * AboutActivity
 * Displays information about the Vacation Home Booking project and team.
 *
 * Developed by: Surbhi Nathji
 */

class AboutActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        val aboutTextView = findViewById<TextView>(R.id.aboutTextView)
        aboutTextView.text = """
            Vacation Home Booking App

            This app is part of a distributed systems project designed to simulate an online platform for booking vacation homes. It demonstrates client-server architecture using microservices.

            Features:
            - Browse vacation home listings
            - Search, filter, and sort homes
            - View home details and submit reviews
            - Mobile-friendly UI with JSON-based data

            Team Members:
            • Surbhi Nathji(33981) - Android App Developer (Client)
            • Aqeel-Sabeeh-Abdulhasan(32621)- Backend Developer (Authentication Microservice)
            • Afolabi Afolayan (27813) - Listings Microservice Developer & Project Lead
            • Nathu (33646)- Admin CLI Tool Developer
           
            Technologies Used:
            - Kotlin, Android SDK
            - RESTful APIs, MongoDB, Django
            - Docker containers for microservices

            Summer Term 2025
            Programming Distributed Systems Course
        """.trimIndent()
    }
}
