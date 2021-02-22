package com.dikascode.avalanche.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dikascode.avalanche.data.Product
import com.dikascode.avalanche.repository.AvalancheRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProductDetailsViewModel: ViewModel() {
    val productDetailsLiveData = MutableLiveData<Product>()

    fun getProductByName(title: String) {
        viewModelScope.launch(Dispatchers.Default){
            productDetailsLiveData.postValue(AvalancheRepositoryImpl().getProductByName(title))
        }
    }
}