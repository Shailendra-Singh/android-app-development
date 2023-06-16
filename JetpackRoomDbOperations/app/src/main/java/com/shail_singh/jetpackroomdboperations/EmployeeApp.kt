package com.shail_singh.jetpackroomdboperations

import android.app.Application

class EmployeeApp : Application() {
    val db by lazy {
        EmployeeDatabase.getInstance(this)
    }
}