package com.codeloop.storeviewapp.features.photo.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.codeloop.storeviewapp.features.photo.domain.model.MediaFileType
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaFileDao {

    @Query("SELECT * FROM ${MediaFolderTable.TABLE_NAME} WHERE ${MediaFolderTable.Columns.TYPE}=:mediaFileType ORDER BY ${MediaFolderTable.Columns.ID} DESC")
    fun getAllMediaFiles(mediaFileType: MediaFileType): List<MediaFolderEntity>

    @Query("SELECT * FROM ${MediaFolderTable.TABLE_NAME} WHERE ${MediaFolderTable.Columns.TYPE}=:mediaFileType ORDER BY ${MediaFolderTable.Columns.ID} DESC")
    fun getAllAsMediaFiles(mediaFileType: MediaFileType): Flow<List<MediaFolderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMediaFile(mediaFile: MediaFolderEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllMediaFiles(mediaFiles: List<MediaFolderEntity>)

    @Query("DELETE FROM ${MediaFolderTable.TABLE_NAME} WHERE ${MediaFolderTable.Columns.ID} = :id")
    suspend fun deleteMediaFile(id: Long)

    @Query("SELECT COUNT(*) FROM ${MediaFolderTable.TABLE_NAME}")
    suspend fun getMediaFileCount(): Int
}
