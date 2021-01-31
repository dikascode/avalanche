package com.decagon.avalanche.constants

import java.text.DecimalFormat

class Constants {

    companion object{
        const val BASE_URL = "https://fcm.googleapis.com"
        const val SERVER_KEY = "AAAAqBjX8wY:APA91bEg-F7_yJnfCJCZJCy2EPC0BrC6zEZ9SEGtI5G604eSmEbJuQmhez4jwMfYMQZuuKnZNH8kAp7S7LvLYpWJMIgDNXI5_hhjUvENkxr3UannI7ZTrJoeFSfQ6IayWYHHaqP0t63W"
        const val CONTENT_TYPE = "application/json"

        /**
         * Formatting prices
         */
        val formatter: DecimalFormat? = DecimalFormat("#,###,###")
    }
}