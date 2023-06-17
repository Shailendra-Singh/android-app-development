package com.shail_singh.happyplaces.models

import java.util.Date

data class HappyPlaceModel(
    val id: Int,
    val title: String,
    val imagePath: String,
    val description: String,
    val date: Date,
    val location: String,
    val latitude: Double,
    val longitude: Double
)
