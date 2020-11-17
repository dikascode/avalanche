package com.decagon.avalanche.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.decagon.avalanche.data.Product
import com.decagon.avalanche.repository.AvalancheRepositoryImpl
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