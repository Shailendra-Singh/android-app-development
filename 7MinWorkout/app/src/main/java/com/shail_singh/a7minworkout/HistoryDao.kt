package com.shail_singh.a7minworkout

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Insert
    suspend fun insert(historyEntity: HistoryEntity)

    @Query("SELECT * FROM `${Constants.TABLE_NAME}` ORDER BY date DESC;")
    fun fetchAll(): Flow<List<HistoryEntity>>
}