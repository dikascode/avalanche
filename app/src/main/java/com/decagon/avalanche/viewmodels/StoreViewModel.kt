package com.decagon.avalanche.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.decagon.avalanche.data.CartItem
import com.decagon.avalanche.data.Product
import com.decagon.avalanche.repository.CartRepo

class StoreViewModel: ViewModel() {
    private val cartRepo = CartRepo()

    fun getCart(): LiveData<List<CartItem>?>? {
        return cartRepo.getCart()
    }

    fun addProductToCart(product: Product): Boolean {
        return cartRepo.addItemToCart(product)
    }
}