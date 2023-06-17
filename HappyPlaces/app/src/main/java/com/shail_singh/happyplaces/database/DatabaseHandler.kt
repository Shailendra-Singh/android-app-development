package com.shail_singh.happyplaces.database

import android.app.Application
import com.shail_singh.happyplaces.HappyPlacesApp
import com.shail_singh.happyplaces.models.HappyPlaceModel
import kotlinx.coroutines.flow.Flow

class DatabaseHandler(application: Application) {

    private var application: Application
    private var happyPlaceDao: HappyPlaceDao

    init {
        this.application = application
        happyPlaceDao = (application as HappyPlacesApp).db.happyPlaceDao()
    }

    suspend fun insertPlace(happyPlaceModel: HappyPlaceModel) {
        happyPlaceDao.insert(happyPlaceModel)
    }

    fun fetchAllPlaces(): Flow<List<HappyPlaceModel>> {
        return happyPlaceDao.fetchAll()
    }

    fun fetchPlace(id: Int): Flow<HappyPlaceModel> {
        return happyPlaceDao.fetchRecord(id)
    }

    suspend fun updatePlace(happyPlaceModel: HappyPlaceModel) {
        happyPlaceDao.update(happyPlaceModel)
    }

    suspend fun deletePlace(happyPlaceModel: HappyPlaceModel) {
        happyPlaceDao.delete(happyPlaceModel)
    }
}