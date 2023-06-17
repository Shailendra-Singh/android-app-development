package com.shail_singh.happyplaces

import android.app.Application
import com.shail_singh.happyplaces.database.HappyPlaceDatabase

class HappyPlacesApp : Application() {
    val db by lazy {
        HappyPlaceDatabase.getInstance(this)
    }
}