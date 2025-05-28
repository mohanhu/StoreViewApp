package com.codeloop.storeviewapp.features.photo.data.local

import android.content.Context
import com.codeloop.storeviewapp.core.data.local.CommonAppDB
import com.codeloop.storeviewapp.features.photo.domain.repository.PhotoLocalRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PhotoLocalRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context,
    private val commonAppDB: CommonAppDB
) : PhotoLocalRepository {
    override fun getAllPhotoFolders(): List<PhotoFolderEntity> {
        return commonAppDB.photoFolderDao.getAllPhotoFolders()
    }

    override fun getAllAsPhotoFolders(): Flow<List<PhotoFolderEntity>> {
        return commonAppDB.photoFolderDao.getAllAsPhotoFolders()
    }

    override suspend fun insertPhotoFolder(photoFolder: PhotoFolderEntity) {
        commonAppDB.photoFolderDao.insertPhotoFolder(photoFolder)
    }

    override suspend fun insertAllPhotoFolder(photoFolder: List<PhotoFolderEntity>) {
        commonAppDB.photoFolderDao.insertAllPhotoFolder(photoFolder)
    }

    override suspend fun deletePhotoFolder(id: Long) {
        commonAppDB.photoFolderDao.deletePhotoFolder(id)
    }

    override suspend fun getPhotoFolderCount(): Int {
        return commonAppDB.photoFolderDao.getPhotoFolderCount()
    }
}