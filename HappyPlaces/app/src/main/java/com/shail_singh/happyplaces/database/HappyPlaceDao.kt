package com.shail_singh.happyplaces.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.shail_singh.happyplaces.AppConstants
import com.shail_singh.happyplaces.models.HappyPlaceModel
import kotlinx.coroutines.flow.Flow

@Dao
interface HappyPlaceDao {
    @Insert
    suspend fun insert(happyPlaceModel: HappyPlaceModel)

    @Update
    suspend fun update(happyPlaceModel: HappyPlaceModel)

    @Delete
    suspend fun delete(happyPlaceModel: HappyPlaceModel)

    @Query("SELECT * FROM `${AppConstants.TABLE_NAME}`;")
    fun fetchAll(): Flow<List<HappyPlaceModel>>

    @Query("SELECT * FROM `${AppConstants.TABLE_NAME}` WHERE id=:id;")
    fun fetchRecord(id: Int): Flow<HappyPlaceModel>
}