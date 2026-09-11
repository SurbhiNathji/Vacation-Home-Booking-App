/**
 * File: HouseDetailActivity.kt
 * Description:
 *    This activity is responsible for displaying the details of a selected house listing,
 *    including its image, title, price, location, and description. It also fetches and displays
 *    user-submitted reviews related to the house and allows authenticated users to submit their own reviews.
 *
 *    The activity uses Retrofit for API communication, Glide for image loading, and a RecyclerView for listing reviews.
 *
 * Author: Surbhi Nathji
 */

package com.example.pdsproject

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pdsproject.model.House
import com.example.pdsproject.model.Review
import com.example.pdsproject.network.RetrofitClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class HouseDetailActivity : ComponentActivity() {

    // UI Components
    private lateinit var imgHouse: ImageView
    private lateinit var txtTitle: TextView
    private lateinit var txtPrice: TextView
    private lateinit var txtLocation: TextView
    private lateinit var txtDescription: TextView
    private lateinit var reviewRecyclerView: RecyclerView
    private lateinit var reviewAdapter: ReviewAdapter
    private val reviews = mutableListOf<Review>()
    private lateinit var etReviewComment: EditText
    private lateinit var submitButton: Button
    lateinit var sharedPreferences: SharedPreferences
    lateinit var reviewerEmail: String
    lateinit var reviewerUsername: String
    lateinit var authHeader: String

    // Unique identifier for the selected house listing
    private var listingId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_house_detail)

        // Initialize view bindings from the layout
        imgHouse = findViewById(R.id.detailImage)
        txtTitle = findViewById(R.id.detailTitle)
        txtPrice = findViewById(R.id.detailPrice)
        txtLocation = findViewById(R.id.location)
        txtDescription = findViewById(R.id.description)
        etReviewComment = findViewById(R.id.etReview)
        submitButton = findViewById(R.id.btnSubmitReview)
        // SharedPreferences should be initialized here
        sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        reviewerEmail = sharedPreferences.getString("user_email", "") ?: ""
        reviewerUsername = sharedPreferences.getString("user_username", "") ?: ""
        val token = sharedPreferences.getString("access_token", null)
        authHeader = "Bearer $token"

        // Set up the RecyclerView to display the list of reviews
        reviewRecyclerView = findViewById(R.id.reviewRecyclerView)
        reviewRecyclerView.layoutManager = LinearLayoutManager(this)
        reviewAdapter = ReviewAdapter(reviews)
        reviewRecyclerView.adapter = reviewAdapter

        // Retrieve the house ID passed via Intent
        listingId = intent.getStringExtra("HOME_ID") ?: ""
        Log.d("submitReview", "Submitting review for listingId: $listingId")

        // If no ID is provided, show an error and exit the activity
        if (listingId.isEmpty()) {
            Toast.makeText(this, "Invalid house ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Load house details and associated reviews
        loadHouseDetails()

        // Set listener for review submission
        submitButton.setOnClickListener {
            val reviewText = etReviewComment.text.toString().trim()

            // Validate input
            if (reviewText.isEmpty()) {
                Toast.makeText(this, "Please enter a review.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Submit the review
            submitReview(reviewText)
        }
    }

    /**
     * Loads house details using Retrofit and updates UI components accordingly.
     */
    private fun loadHouseDetails() {
        RetrofitClient.instance.getHouseDetails(authHeader,listingId)
            .enqueue(object : Callback<House> {
                override fun onResponse(call: Call<House>, response: Response<House>) {
                    if (response.isSuccessful && response.body() != null) {
                        val house = response.body()!!

                        // Load the house image using Glide
                        Glide.with(this@HouseDetailActivity)
                            .load(house.imageUrl)
                            .into(imgHouse)

                        // Populate house details
                        txtTitle.text = house.title
                        txtPrice.text = "$${house.price}"
                        txtLocation.text = house.location
                        txtDescription.text = house.description

                        // Update the review list
                        reviews.clear()
                        reviews.addAll(house.reviews ?: emptyList())
                        reviewAdapter.notifyDataSetChanged()
                    } else {
                        Log.e("HouseDetailActivity", "Failed to load: ${response.code()} - ${response.errorBody()?.string()}")
                        Toast.makeText(this@HouseDetailActivity, "Failed to load house details.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<House>, t: Throwable) {
                    Log.e("HouseDetailActivity", "Error: ${t.message}", t)
                    Toast.makeText(this@HouseDetailActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    /**
     * Submits a new review using Retrofit and refreshes the review list on success.
     *
     * @param comment The text content of the review submitted by the user.
     */
    private fun submitReview(comment: String) {
        // Format the current date
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dateString = dateFormat.format(Date())



        // Create a Review object to send
        val review = Review(
            reviewer_id = reviewerEmail,
            reviewerName = reviewerUsername,
            comments = comment,
            date = dateString,
        )

        Log.d("submitReview", "Sending review with listingId='$listingId'")

        // Call the API to submit the review
        RetrofitClient.instance.addReview(authHeader,listingId, review)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@HouseDetailActivity, "Review submitted!", Toast.LENGTH_SHORT).show()
                        etReviewComment.text.clear()
                        loadHouseDetails()  // Refresh the displayed reviews
                    } else {
                        Log.e("HouseDetailActivity", "Failed to submit review: ${response.code()} - ${response.errorBody()?.string()}")
                        Toast.makeText(this@HouseDetailActivity, "Failed to submit review.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("HouseDetailActivity", "Error submitting review: ${t.message}", t)
                    Toast.makeText(this@HouseDetailActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
