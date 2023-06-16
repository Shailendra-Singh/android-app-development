package com.shail_singh.jetpackroomdboperations

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "${Constants.TABLE_NAME}")
data class EmployeeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, val email: String, val name: String
)