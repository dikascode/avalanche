package com.decagon.avalanche.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.decagon.avalanche.R
import com.decagon.avalanche.data.Product
import com.decagon.avalanche.ui.ProductDetails
import com.squareup.picasso.Picasso

class ProductsAdapter(private val products: ArrayList<Product>) :
    RecyclerView.Adapter<ProductsAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = products[position]
        Picasso.get().load(product.photoUrl).into(holder.image)
        holder.title.text = product.title
        holder.price.text = product.price.toString()

        if(product.isOnSale) holder.isOnSaleIcon.visibility = View.VISIBLE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.product_row, parent, false)
        val holder = ViewHolder(view)
        val intent = Intent(parent.context, ProductDetails::class.java)

        view.setOnClickListener {
            intent.putExtra("title", products[holder.adapterPosition].title)
            intent.putExtra("photo_url", products[holder.adapterPosition].photoUrl)
            parent.context.startActivity(intent)
        }
        return holder
    }

    override fun getItemCount() = products.size


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.product_row_iv)
        val title: TextView = itemView.findViewById(R.id.product_title_tv)
        val price: TextView = itemView.findViewById(R.id.product_price)
        val isOnSaleIcon: ImageView = itemView.findViewById(R.id.isOnSaleIcon)

    }
}
