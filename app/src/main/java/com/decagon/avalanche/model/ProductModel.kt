package com.decagon.avalanche.model


import com.decagon.avalanche.room.ProductDao
import com.decagon.avalanche.room.RoomProduct

class ProductModel(private  val productDao: ProductDao) {

    fun addProduct(product: RoomProduct) {
        productDao.insertAll(product)
    }

    fun readAllProducts(): List<RoomProduct> {
        return productDao.getAll()
    }

    fun getSearchProducts(search: String): List<RoomProduct> {
        return productDao.homeSearch(search)
    }
}