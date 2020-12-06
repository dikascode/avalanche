package com.decagon.avalanche.data

import android.widget.Spinner
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.DiffUtil


data class CartItem(var product: Product, var quantity: Int) {

    override fun toString(): String {
        return "CartItem{" +
                "product=" + product +
                ", quantity=" + quantity +
                '}'
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val cartItem = other as CartItem
        return quantity == cartItem.quantity && product == cartItem.product
    }

    override fun hashCode(): Int {
        var result = product.hashCode()
        result = 31 * result + quantity
        return result
    }

    companion object {
        var itemCallback: DiffUtil.ItemCallback<CartItem> =
            object : DiffUtil.ItemCallback<CartItem>() {
                override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
                    return oldItem.quantity == newItem.quantity
                }

                override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
                    return oldItem == newItem
                }
            }
    }
}

@BindingAdapter("android:setVal")
fun getSelectedSpinnerValue(spinner: Spinner, quantity: Int) {
    spinner.setSelection(quantity - 1, true)
}

