/**
 * File: MainActivity.kt
 * Description:
 *    This is the main entry screen of the Vacation Home Booking App.
 *    It fetches a list of vacation homes from a remote API and displays them using a RecyclerView.
 *    Users can search, filter by price and location, and sort listings.
 *
 * Features:
 *    - Fetch data via Retrofit from backend service
 *    - Dynamic search by title
 *    - Filtering by price range and location
 *    - Sorting by price (ascending or descending)
 *    - Toggleable filter UI
 *    - Navigation to About screen
 *
 * Developed by: Surbhi Nathji
 */

package com.example.pdsproject

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pdsproject.model.House
import com.example.pdsproject.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : ComponentActivity() {

    // RecyclerView adapter to display the list of houses
    private lateinit var adapter: HouseAdapter

    // List of houses from API (immutable source data)
    private var houseList: List<House> = emptyList()

    // List after applying search, filter, and sort
    private lateinit var filteredList: MutableList<House>

    // RecyclerView UI element
    private lateinit var recyclerView: RecyclerView

    // Current UI state for filters and search
    private var currentSearchText = ""
    private var currentMinPrice = 0
    private var currentMaxPrice = Int.MAX_VALUE
    private var currentLocation = "All"
    private var currentSortBy = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Bind UI components
        val searchView = findViewById<EditText>(R.id.searchBar)
        recyclerView = findViewById(R.id.recyclerView)
        val filterSection = findViewById<LinearLayout>(R.id.filterSection)
        val filterButton = findViewById<Button>(R.id.filterButton)
        val btnApplyFilters = findViewById<Button>(R.id.btnApplyFilters)
        val etMinPrice = findViewById<EditText>(R.id.etMinPrice)
        val etMaxPrice = findViewById<EditText>(R.id.etMaxPrice)
        val spnSort = findViewById<Spinner>(R.id.spnSort)
        val spnLocation = findViewById<Spinner>(R.id.spnLocation)
        val aboutButton = findViewById<Button>(R.id.aboutButton)

        // Navigate to AboutActivity
        aboutButton.setOnClickListener {
            val intent = Intent(this, AboutActivity::class.java)
            startActivity(intent)
        }

        // Initialize RecyclerView and adapter
        adapter = HouseAdapter(this@MainActivity, this@MainActivity)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Initialize sorting options
        val sortOptions = listOf("Price: Low to High", "Price: High to Low")
        spnSort.adapter = ArrayAdapter(this, R.layout.simple_spinner_item, sortOptions)

        // Retrieve the token from SharedPreferences
        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        //to send token
        val token= sharedPreferences.getString("access_token",null)
        Log.d("TOKEN_RETRIEVED", "Token: $token")
        val authHeader= "Bearer $token"
        Log.d("token", "token: $authHeader")

        // Fetch houses from backend using Retrofit
        RetrofitClient.instance.getHouseSummaries(authHeader).enqueue(object : Callback<List<House>> {
            override fun onResponse(call: Call<List<House>>, response: Response<List<House>>) {
                if (response.isSuccessful && response.body() != null) {
                    houseList = response.body()!!
                    filteredList = houseList.toMutableList()
                    adapter.updateData(filteredList)

                    // Dynamically build location filter list
                    val locations = listOf("All") + houseList.map { it.location }.distinct()
                    val locationAdapter = ArrayAdapter(this@MainActivity, R.layout.simple_spinner_item, locations)
                    locationAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                    spnLocation.adapter = locationAdapter
                } else {
                    Toast.makeText(this@MainActivity, "Failed to load listings", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<House>>, t: Throwable) {
                Log.d("Listings", "Error: ${t.localizedMessage}")
                Toast.makeText(this@MainActivity, "Error: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        })

        // Live search: filter list as user types in search bar
        searchView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                currentSearchText = s.toString()
                applyAllFilters()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Toggle visibility of filter section
        filterButton.setOnClickListener {
            filterSection.visibility =
                if (filterSection.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }

        // Apply filters when "Apply Filters" button is clicked
        btnApplyFilters.setOnClickListener {
            currentMinPrice = etMinPrice.text.toString().toIntOrNull() ?: 0
            currentMaxPrice = etMaxPrice.text.toString().toIntOrNull() ?: Int.MAX_VALUE
            currentLocation = spnLocation.selectedItem?.toString() ?: "All"
            currentSortBy = spnSort.selectedItem?.toString() ?: ""
            applyAllFilters()
        }
    }

    /**
     * Applies current filters, search text, and sorting to the full house list.
     * Updates the RecyclerView adapter with the filtered results.
     */
    fun applyAllFilters() {
        var tempList = houseList

        // Filter by price range and selected location
        tempList = tempList.filter {
            it.price.toInt() in currentMinPrice..currentMaxPrice &&
                    (currentLocation == "All" || it.location == currentLocation)
        }

        // Filter by search query in the title (case-insensitive)
        tempList = tempList.filter {
            it.title.contains(currentSearchText, ignoreCase = true)
        }

        // Apply sorting by price
        tempList = when (currentSortBy) {
            "Price: Low to High" -> tempList.sortedBy { it.price }
            "Price: High to Low" -> tempList.sortedByDescending { it.price }
            else -> tempList
        }

        // Update adapter with new filtered list
        filteredList.clear()
        filteredList.addAll(tempList)
        adapter.updateData(filteredList)
    }
}
