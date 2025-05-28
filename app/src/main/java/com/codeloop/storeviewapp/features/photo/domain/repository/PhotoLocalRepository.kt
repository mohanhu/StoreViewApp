package com.codeloop.storeviewapp.features.photo.domain.repository

import com.codeloop.storeviewapp.features.photo.data.local.PhotoFolderEntity
import kotlinx.coroutines.flow.Flow

interface PhotoLocalRepository {

    fun getAllPhotoFolders(): List<PhotoFolderEntity>

    fun getAllAsPhotoFolders(): Flow<List<PhotoFolderEntity>>

    suspend fun insertPhotoFolder(photoFolder: PhotoFolderEntity)
    suspend fun insertAllPhotoFolder(photoFolder: List<PhotoFolderEntity>)

    suspend fun deletePhotoFolder(id: Long)

    suspend fun getPhotoFolderCount(): Int
}