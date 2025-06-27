package com.codeloop.storeviewapp.features.photo.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoFolderDao {
    @Query("SELECT * FROM ${PhotoFolderTable.TABLE_NAME} ORDER BY ${PhotoFolderTable.Columns.CREATED_AT} ASC")
    fun getAllPhotoFolders(): List<PhotoFolderEntity>

    @Query("SELECT * FROM ${PhotoFolderTable.TABLE_NAME} ORDER BY ${PhotoFolderTable.Columns.CREATED_AT} ASC")
    fun getAllAsPhotoFolders(): Flow<List<PhotoFolderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhotoFolder(photoFolder: PhotoFolderEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllPhotoFolder(photoFolder: List<PhotoFolderEntity>)

    @Query("DELETE FROM ${PhotoFolderTable.TABLE_NAME} WHERE ${PhotoFolderTable.Columns.ID} = :id")
    suspend fun deletePhotoFolder(id: Long)

    @Query("SELECT COUNT(*) FROM ${PhotoFolderTable.TABLE_NAME}")
    suspend fun getPhotoFolderCount(): Int
}