package com.codeloop.storeviewapp.features.video.data.local

import android.content.Context
import com.codeloop.storeviewapp.core.data.local.CommonAppDB
import com.codeloop.storeviewapp.features.video.domain.repository.VideoLocalRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class VideoLocalRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context,
    private val commonAppDB: CommonAppDB
) : VideoLocalRepository {

    override fun getAllVideoFolders(): List<VideoFolderEntity> {
        return commonAppDB.videoFolderDao.getAllVideoFolders()
    }

    override fun getAllAsVideoFolders(): Flow<List<VideoFolderEntity>> {
        return commonAppDB.videoFolderDao.getAllAsVideoFolders()
    }

    override suspend fun insertVideoFolder(videoFolder: VideoFolderEntity) {
        commonAppDB.videoFolderDao.insertVideoFolder(videoFolder)
    }

    override suspend fun insertAllVideoFolder(videoFolder: List<VideoFolderEntity>) {
        commonAppDB.videoFolderDao.insertAllVideoFolder(videoFolder)
    }

    override suspend fun deleteVideoFolder(id: Long) {
        commonAppDB.videoFolderDao.deleteVideoFolder(id)
    }

    override suspend fun getVideoFolderCount(): Int {
        return commonAppDB.videoFolderDao.getVideoFolderCount()
    }
}
