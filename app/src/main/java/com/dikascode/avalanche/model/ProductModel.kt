package com.dikascode.avalanche.model


import com.dikascode.avalanche.room.ProductDao
import com.dikascode.avalanche.room.RoomProductModel

class ProductModel(private  val productDao: ProductDao) {

    fun addProduct(product: RoomProductModel) {
        productDao.insertAll(product)
    }

    fun readAllProducts(): List<RoomProductModel> {
        return productDao.getAll()
    }

    fun getSearchProducts(search: String): List<RoomProductModel> {
        return productDao.homeSearch(search)
    }
}