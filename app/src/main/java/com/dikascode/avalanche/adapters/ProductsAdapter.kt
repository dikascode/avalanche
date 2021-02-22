package com.dikascode.avalanche.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.RecyclerView
import com.dikascode.avalanche.R
import com.dikascode.avalanche.constants.Constants.Companion.FORMATTER
import com.dikascode.avalanche.data.Product
import com.dikascode.avalanche.viewmodels.StoreViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso

class ProductsAdapter(
    private val products: ArrayList<Product>,
    private val viewModelStoreOwner: ViewModelStoreOwner,
    private val onClickProduct: (title: String, photoUrl: String, photoView: View) -> Unit,
) :
    RecyclerView.Adapter<ProductsAdapter.ViewHolder>() {
    lateinit var storeViewModel: StoreViewModel

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        storeViewModel = ViewModelProvider(viewModelStoreOwner).get(StoreViewModel::class.java)
        val product = products[position]
        Picasso.get().load(product.photoUrl).into(holder.image)
        holder.title.text = product.title

        val price = FORMATTER?.format(product.price)
        holder.price.text = price.toString()

//        if (product.isOnSale) holder.isOnSaleIcon.visibility = View.VISIBLE

        //Invoke onClickProduct on click of image
        holder.image.setOnClickListener {
            onClickProduct.invoke(product.title, product.photoUrl, holder.image)
        }

        holder.addToCart.setOnClickListener {
            //Check if product has been added to cart
            val isAdded: Boolean = storeViewModel.addProductToCart(product)

            if (isAdded) {
                Snackbar.make(
                    holder.itemView,
                    "${product.title} added to cart.",
                    Snackbar.LENGTH_LONG
                )
//                    .setAction("Checkout") {
//                    holder.itemView.findNavController().navigate(R.id.cartFragment)
//                }.show()
            } else {
                Snackbar.make(
                    holder.itemView,
                    "Max quantity of 5 for item reached in cart",
                    Snackbar.LENGTH_LONG
                ).show()
            }
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
        val addToCart: MaterialButton = itemView.findViewById(R.id.add_product_to_cart_btn)

    }
}
