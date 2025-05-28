package com.codeloop.storeviewapp.features.photo.domain.repository

import com.codeloop.storeviewapp.features.photo.data.local.MediaFolderEntity
import com.codeloop.storeviewapp.features.photo.domain.model.MediaFileType
import kotlinx.coroutines.flow.Flow

interface MediaFileRepository {
    fun getAllMediaFiles(mediaFileType: MediaFileType): List<MediaFolderEntity>
    fun getAllAsMediaFiles(mediaFileType: MediaFileType): Flow<List<MediaFolderEntity>>
    suspend fun insertMediaFile(mediaFile: MediaFolderEntity)
    suspend fun insertAllMediaFiles(mediaFiles: List<MediaFolderEntity>)
    suspend fun deleteMediaFile(id: Long)
    suspend fun getMediaFileCount(): Int
}