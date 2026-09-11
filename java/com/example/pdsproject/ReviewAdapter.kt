package com.example.pdsproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pdsproject.model.Review

/**
 * Adapter to display a list of reviews in a RecyclerView.
 *
 * Responsibilities:
 * - Inflate review item layout
 * - Bind review data (reviewer name, comments, date) to UI
 * - Handle the review list size
 *
 * @param reviews MutableList of Review objects to display
 */
class ReviewAdapter(
    private val reviews: MutableList<Review>
) : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    class ReviewViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val reviewerName: TextView = view.findViewById(R.id.reviewerName)
        val comments: TextView = view.findViewById(R.id.reviewComments)
        val date: TextView = view.findViewById(R.id.reviewDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_review, parent, false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviews[position]
        holder.reviewerName.text = review.reviewerName ?: "Anonymous"
        holder.comments.text = review.comments
        holder.date.text = review.date ?: ""
    }

    override fun getItemCount() = reviews.size
}
