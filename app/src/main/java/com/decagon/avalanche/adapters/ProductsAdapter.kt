package com.decagon.avalanche.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.decagon.avalanche.R
import com.decagon.avalanche.data.Product
import com.squareup.picasso.Picasso

class ProductsAdapter(
    private val products: ArrayList<Product>,
    private val onClickProduct: (title: String, photoUrl: String, photoView: View) -> Unit
) :
    RecyclerView.Adapter<ProductsAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = products[position]
        Picasso.get().load(product.photoUrl).into(holder.image)
        holder.title.text = product.title
        holder.price.text = product.price.toString()

        if (product.isOnSale) holder.isOnSaleIcon.visibility = View.VISIBLE

        //Invoke onClickProduct on click of image
        holder.image.setOnClickListener {
            onClickProduct.invoke(product.title, product.photoUrl, holder.image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.product_row, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = products.size


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.product_row_iv)
        val title: TextView = itemView.findViewById(R.id.product_title_tv)
        val price: TextView = itemView.findViewById(R.id.product_price)
        val isOnSaleIcon: ImageView = itemView.findViewById(R.id.isOnSaleIcon)

    }
}
