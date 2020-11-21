package com.decagon.avalanche.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.decagon.avalanche.data.Product
import com.decagon.avalanche.repository.AvalancheRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProductsListViewModel : ViewModel() {
    //Setup live data to be observed in activity or fragment class
    val productsLiveData = MutableLiveData<List<Product>>()
    val productsSearchLiveData = MutableLiveData<List<Product>>()


    fun showProducts() {
        viewModelScope.launch(Dispatchers.IO) {
            productsLiveData.postValue(AvalancheRepositoryImpl().getAllProducts())
        }
    }


    fun searchProducts(term: String) {
        viewModelScope.launch(Dispatchers.IO) {
            productsSearchLiveData.postValue(AvalancheRepositoryImpl().searchForProducts(term))
        }
    }

}