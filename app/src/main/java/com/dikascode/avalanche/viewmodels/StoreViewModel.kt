package com.dikascode.avalanche.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.dikascode.avalanche.data.CartItem
import com.dikascode.avalanche.data.Product
import com.dikascode.avalanche.repository.CartRepo


class StoreViewModel: ViewModel() {
    private val cartRepo = CartRepo()

    fun getCart(): LiveData<List<CartItem>?>? {
        return cartRepo.getCart()
    }

    fun addProductToCart(product: Product): Boolean {
        return cartRepo.addItemToCart(product)
    }

    fun removeItemFromCart(cartItem: CartItem?) {
        cartRepo.removeItemFromCart(cartItem!!)
    }

    fun changeQuantity(cartItem: CartItem?, quantity: Int) {
        cartRepo.changeQuantity(cartItem!!, quantity)
    }

    fun getTotalPrice(): LiveData<Double?>? {
        return cartRepo.getTotalPrice()
    }

    fun resetCart() {
        cartRepo.initCart()
    }
}