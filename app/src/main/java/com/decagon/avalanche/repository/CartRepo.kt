package com.decagon.avalanche.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.decagon.avalanche.data.CartItem
import com.decagon.avalanche.data.Product


class CartRepo {
    private val mutableCart = MutableLiveData<List<CartItem>?>()
    private val mutableTotalPrice = MutableLiveData<Double?>()

    fun getCart(): LiveData<List<CartItem>?>? {
        if (mutableCart.value == null) {
            initCart()
        }
        return mutableCart
    }

    private fun initCart() {
        mutableCart.value = ArrayList()
        calculateCartTotal()
    }

    fun addItemToCart(product: Product): Boolean {
        if (mutableCart.value == null) {
            initCart()
        }
        val cartItemList: MutableList<CartItem> = ArrayList(mutableCart.value!!)
        for (cartItem in cartItemList) {
            if (cartItem.product.title == product.title) {
                if (cartItem.quantity == 5) {
                    return false
                }
                val index = cartItemList.indexOf(cartItem)
                cartItem.quantity = cartItem.quantity + 1
                cartItemList[index] = cartItem
                mutableCart.value = cartItemList
                calculateCartTotal()
                return true
            }
        }

        val cartItem = CartItem(product, 1)
        cartItemList.add(cartItem)
        mutableCart.value = cartItemList
        calculateCartTotal()
        return true
    }

    fun removeItemFromCart(cartItem: CartItem) {
        if (mutableCart.value == null) {
            return
        }
        val cartItemList: MutableList<CartItem> = ArrayList(mutableCart.value!!)
        cartItemList.remove(cartItem)
        mutableCart.value = cartItemList
        calculateCartTotal()
    }

    fun changeQuantity(cartItem: CartItem, quantity: Int) {
        if (mutableCart.value == null) return
        val cartItemList: MutableList<CartItem> = ArrayList(mutableCart.value!!)
        val updatedItem = CartItem(cartItem.product, quantity)
        cartItemList[cartItemList.indexOf(cartItem)] = updatedItem
        mutableCart.value = cartItemList
        calculateCartTotal()
    }

    private fun calculateCartTotal() {
        if (mutableCart.value == null) return
        var total = 0.0
        val cartItemList = mutableCart.value
        for ((product, quantity) in cartItemList!!) {
            total += product.price * quantity
        }
        mutableTotalPrice.value = total
    }

    fun getTotalPrice(): LiveData<Double?>? {
        if (mutableTotalPrice.value == null) {
            mutableTotalPrice.value = 0.0
        }
        return mutableTotalPrice
    }
}