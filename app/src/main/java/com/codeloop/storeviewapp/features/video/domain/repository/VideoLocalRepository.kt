package com.codeloop.storeviewapp.features.video.domain.repository

import com.codeloop.storeviewapp.features.video.data.local.VideoFolderEntity
import kotlinx.coroutines.flow.Flow

interface VideoLocalRepository {

    fun getAllVideoFolders(): List<VideoFolderEntity>

    fun getAllAsVideoFolders(): Flow<List<VideoFolderEntity>>

    suspend fun insertVideoFolder(videoFolder: VideoFolderEntity)
    suspend fun insertAllVideoFolder(videoFolder: List<VideoFolderEntity>)

    suspend fun deleteVideoFolder(id: Long)

    suspend fun getVideoFolderCount(): Int
}
