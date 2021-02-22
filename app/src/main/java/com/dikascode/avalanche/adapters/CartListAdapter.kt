package com.dikascode.avalanche.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dikascode.avalanche.data.CartItem
import com.dikascode.avalanche.databinding.CartRowBinding
import com.squareup.picasso.Picasso


class CartListAdapter(val cartInterface: CartInterface) :
    ListAdapter<CartItem, CartListAdapter.CartVH?>(CartItem.itemCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartVH {
        val layoutInflater = LayoutInflater.from(parent.context)
        val cartRowBinding = CartRowBinding.inflate(layoutInflater, parent, false)
        return CartVH(cartRowBinding)
    }

    override fun onBindViewHolder(holder: CartVH, position: Int) {
        holder.cartRowBinding.cartItem = getItem(position)
        holder.cartRowBinding.executePendingBindings()
        Picasso.get().load(getItem(position).product.photoUrl).into(holder.cartRowBinding.cartProductImageView)
    }


    inner class CartVH(var cartRowBinding: CartRowBinding) : RecyclerView.ViewHolder(
        cartRowBinding.root
    ) {
        init {
            cartRowBinding.deleteProductBtn.setOnClickListener {
                cartInterface.deleteItem(
                    getItem(adapterPosition)
                )
            }
            cartRowBinding.quantitySpinner.onItemSelectedListener =
                object : OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View,
                        position: Int,
                        id: Long
                    ) {
                        val quantity = position + 1
                        if (quantity == getItem(adapterPosition)!!.quantity) {
                            return
                        }
                        cartInterface.changeQuantity(getItem(adapterPosition), quantity)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
        }
    }

    interface CartInterface {
        fun deleteItem(cartItem: CartItem?)
        fun changeQuantity(cartItem: CartItem?, quantity: Int)
    }
}