package com.codeloop.storeviewapp.features.photo.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.codeloop.storeviewapp.features.photo.domain.model.MediaFileType
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaFileDao {

    @Transaction
    @Query("SELECT * FROM ${MediaFolderTable.TABLE_NAME} WHERE ${MediaFolderTable.Columns.TYPE}=:mediaFileType AND ${MediaFolderTable.Columns.RELATIVE_PATH}=:relativePath ORDER BY ${MediaFolderTable.Columns.ID} DESC")
    fun getAllMediaFiles(mediaFileType: MediaFileType, relativePath: String): List<MediaFolderEntity>

    @Transaction
    @Query("SELECT * FROM ${MediaFolderTable.TABLE_NAME} WHERE ${MediaFolderTable.Columns.TYPE}=:mediaFileType AND ${MediaFolderTable.Columns.RELATIVE_PATH}=:relativePath ORDER BY ${MediaFolderTable.Columns.ID} DESC")
    fun getAllAsMediaFiles(mediaFileType: MediaFileType, relativePath: String): Flow<List<MediaFolderEntity>>

    @Transaction
    @Query("SELECT * FROM ${MediaFolderTable.TABLE_NAME} WHERE ${MediaFolderTable.Columns.TYPE}=:mediaFileType ORDER BY ${MediaFolderTable.Columns.ID} DESC")
    fun getAllAsMedia(mediaFileType: MediaFileType): Flow<List<MediaFolderEntity>>

    @Transaction
    @Query("SELECT * FROM ${MediaFolderTable.TABLE_NAME} WHERE ${MediaFolderTable.Columns.TYPE}=:mediaFileType ORDER BY ${MediaFolderTable.Columns.ID} DESC")
    fun getAllAsMediaData(mediaFileType: MediaFileType): List<MediaFolderEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMediaFile(mediaFile: MediaFolderEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllMediaFiles(mediaFiles: List<MediaFolderEntity>)

    @Query("DELETE FROM ${MediaFolderTable.TABLE_NAME} WHERE ${MediaFolderTable.Columns.ID} = :id")
    suspend fun deleteMediaFile(id: Long)

    @Transaction
    @Query("DELETE FROM ${MediaFolderTable.TABLE_NAME} WHERE ${MediaFolderTable.Columns.RELATIVE_PATH} = :path")
    suspend fun deleteMediaFilePath(path: String)

    @Query("DELETE FROM ${MediaFolderTable.TABLE_NAME}")
    suspend fun deleteAllMediaFiles()

    @Query("SELECT COUNT(*) FROM ${MediaFolderTable.TABLE_NAME}")
    suspend fun getMediaFileCount(): Int
}
