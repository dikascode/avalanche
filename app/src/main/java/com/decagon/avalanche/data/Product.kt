package com.decagon.avalanche.data

import com.google.gson.annotations.SerializedName

class Product {

    var title: String = "Short"
    var photoUrl: String = "https://finepointmobile.com/api/ecommerce/redHat.jpg"
    var price: Double = 192.95
    var desc: String =
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. "
    var isOnSale: Boolean = true

    constructor() {

    }

    constructor(title: String, photoUrl: String, price: Double, desc: String, isOnSale: Boolean ) {
        this.title = title
        this.photoUrl = photoUrl
        this.price = price
        this.desc = desc
        this.isOnSale = isOnSale
    }
}