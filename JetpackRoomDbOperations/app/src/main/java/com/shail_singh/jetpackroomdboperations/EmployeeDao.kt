package com.shail_singh.jetpackroomdboperations

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface EmployeeDao {
    @Insert
    suspend fun insert(employeeEntity: EmployeeEntity)

    @Update
    suspend fun update(employeeEntity: EmployeeEntity)

    @Delete
    suspend fun delete(employeeEntity: EmployeeEntity)

    @Query("SELECT * FROM `${Constants.TABLE_NAME}`;")
    fun fetchAll(): Flow<List<EmployeeEntity>>

    @Query("SELECT * FROM `${Constants.TABLE_NAME}` WHERE id=:id;")
    fun fetchEmployee(id: Int): Flow<EmployeeEntity>
}