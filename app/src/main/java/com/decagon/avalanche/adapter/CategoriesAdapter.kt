package com.decagon.avalanche.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.decagon.avalanche.R

class CategoriesAdapter(private val categories: List<String>) : RecyclerView.Adapter<CategoriesAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.category_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.categoryName.text = categories[position]
    }

    override fun getItemCount() = categories.size

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val categoryName = view.findViewById<TextView>(R.id.category_name)
    }
}
