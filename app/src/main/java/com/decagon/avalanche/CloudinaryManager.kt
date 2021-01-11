package com.decagon.avalanche

import android.app.Application
import com.cloudinary.android.MediaManager


class CloudinaryManager() : Application() {
        var config: HashMap<String, String> = HashMap()

        override fun onCreate() {
                super.onCreate()
                //initialize MediaManager
                config["cloud_name"] = "di2lpinnp"
                config["api_key"] = "396379412919671"
                config["api_secret"] = "LPNhun_GmRbaVOGVYRosAkacJds"
                MediaManager.init(this, config)

        }
}