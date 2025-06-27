package com.codeloop.storeviewapp.features.photo.data.local

import android.content.Context
import com.codeloop.storeviewapp.core.data.local.CommonAppDB
import com.codeloop.storeviewapp.features.photo.domain.model.MediaFileType
import com.codeloop.storeviewapp.features.photo.domain.repository.MediaFileLocalRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MediaFileLocalRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context,
    private val commonAppDB: CommonAppDB
) : MediaFileLocalRepository {

    override fun getAllMediaFiles(mediaFileType: MediaFileType,relativePath:String): List<MediaFolderEntity> {
        return if (relativePath.isEmpty()){
            commonAppDB.mediaFolderDao.getAllAsMediaData(mediaFileType)
        }
        else{
            commonAppDB.mediaFolderDao.getAllMediaFiles(mediaFileType,relativePath)
        }
    }

    override fun getAllAsMediaFiles(mediaFileType: MediaFileType,relativePath:String): Flow<List<MediaFolderEntity>> {
        return if (relativePath.isEmpty()){
            commonAppDB.mediaFolderDao.getAllAsMedia(mediaFileType)
        }
        else{
            commonAppDB.mediaFolderDao.getAllAsMediaFiles(mediaFileType,relativePath)
        }
    }

    override suspend fun insertMediaFile(mediaFile: MediaFolderEntity) {
        commonAppDB.mediaFolderDao.insertMediaFile(mediaFile)
    }

    override suspend fun insertAllMediaFiles(mediaFiles: List<MediaFolderEntity>) {
        commonAppDB.mediaFolderDao.insertAllMediaFiles(mediaFiles)
    }

    override suspend fun deleteMediaFile(id: Long) {
        commonAppDB.mediaFolderDao.deleteMediaFile(id)
    }
    override suspend fun deleteMediaFilePath(path: String) {
        commonAppDB.mediaFolderDao.deleteMediaFilePath(path)
    }

    override suspend fun deleteAllMediaFiles() {
        commonAppDB.mediaFolderDao.deleteAllMediaFiles()
    }

    override suspend fun getMediaFileCount(): Int {
        return commonAppDB.mediaFolderDao.getMediaFileCount()
    }
}
