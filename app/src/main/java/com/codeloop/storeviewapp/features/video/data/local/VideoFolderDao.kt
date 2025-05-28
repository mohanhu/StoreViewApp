package com.codeloop.storeviewapp.features.video.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoFolderDao {
    @Query("SELECT * FROM ${VideoFolderTable.TABLE_NAME} ORDER BY ${VideoFolderTable.Columns.CREATED_AT} DESC")
    fun getAllVideoFolders(): List<VideoFolderEntity>

    @Query("SELECT * FROM ${VideoFolderTable.TABLE_NAME} ORDER BY ${VideoFolderTable.Columns.CREATED_AT} DESC")
    fun getAllAsVideoFolders(): Flow<List<VideoFolderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideoFolder(videoFolder: VideoFolderEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllVideoFolder(videoFolder: List<VideoFolderEntity>)

    @Query("DELETE FROM ${VideoFolderTable.TABLE_NAME} WHERE ${VideoFolderTable.Columns.ID} = :id")
    suspend fun deleteVideoFolder(id: Long)

    @Query("SELECT COUNT(*) FROM ${VideoFolderTable.TABLE_NAME}")
    suspend fun getVideoFolderCount(): Int
}
