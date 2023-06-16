package com.shail_singh.a7minworkout

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = Constants.TABLE_NAME)
data class HistoryEntity(
    @PrimaryKey val date: String
)