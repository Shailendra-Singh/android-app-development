package com.shail_singh.happyplaces.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shail_singh.happyplaces.AppConstants
import java.io.Serializable

@Entity(tableName = AppConstants.TABLE_NAME)
data class HappyPlaceModel(
    @PrimaryKey val id: Long = System.currentTimeMillis(),
    val title: String,
    val imagePath: String,
    val description: String,
    val date: String,
    val location: String,
    val latitude: Double,
    val longitude: Double
) : Serializable
