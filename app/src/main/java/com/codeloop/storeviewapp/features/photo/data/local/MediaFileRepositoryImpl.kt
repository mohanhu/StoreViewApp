package com.codeloop.storeviewapp.features.photo.data.local

import android.content.Context
import com.codeloop.storeviewapp.core.data.local.CommonAppDB
import com.codeloop.storeviewapp.features.photo.domain.model.MediaFileType
import com.codeloop.storeviewapp.features.photo.domain.repository.MediaFileRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MediaFileRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context,
    private val commonAppDB: CommonAppDB
) : MediaFileRepository {

    override fun getAllMediaFiles(mediaFileType: MediaFileType): List<MediaFolderEntity> {
        return commonAppDB.mediaFolderDao.getAllMediaFiles(mediaFileType)
    }

    override fun getAllAsMediaFiles(mediaFileType: MediaFileType): Flow<List<MediaFolderEntity>> {
        return commonAppDB.mediaFolderDao.getAllAsMediaFiles(mediaFileType)
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

    override suspend fun getMediaFileCount(): Int {
        return commonAppDB.mediaFolderDao.getMediaFileCount()
    }
}
