package com.codeloop.storeviewapp.features.photo.domain.repository

import com.codeloop.storeviewapp.features.photo.data.local.MediaFolderEntity
import com.codeloop.storeviewapp.features.photo.domain.model.MediaFileType
import kotlinx.coroutines.flow.Flow

interface MediaFileLocalRepository {
    fun getAllMediaFiles(mediaFileType: MediaFileType,relativePath:String): List<MediaFolderEntity>
    fun getAllAsMediaFiles(mediaFileType: MediaFileType,relativePath:String): Flow<List<MediaFolderEntity>>
    suspend fun insertMediaFile(mediaFile: MediaFolderEntity)
    suspend fun insertAllMediaFiles(mediaFiles: List<MediaFolderEntity>)
    suspend fun deleteMediaFile(id: Long)
    suspend fun deleteMediaFilePath(path: String)
    suspend fun deleteAllMediaFiles()
    suspend fun getMediaFileCount(): Int
}