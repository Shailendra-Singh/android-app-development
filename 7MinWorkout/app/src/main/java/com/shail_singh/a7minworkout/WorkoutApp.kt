package com.shail_singh.a7minworkout

import android.app.Application

class WorkoutApp : Application() {
    val db by lazy {
        HistoryDatabase.getInstance(this)
    }
}