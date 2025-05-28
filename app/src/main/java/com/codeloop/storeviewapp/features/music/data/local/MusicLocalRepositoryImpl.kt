package com.codeloop.storeviewapp.features.music.data.local

import android.content.Context
import com.codeloop.storeviewapp.core.data.local.CommonAppDB
import com.codeloop.storeviewapp.features.music.domain.repository.MusicLocalRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MusicLocalRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context,
    private val commonAppDB: CommonAppDB
) : MusicLocalRepository {

    override fun getAllMusicFolders(): List<MusicFolderEntity> {
        return commonAppDB.musicFolderDao.getAllMusicFolders()
    }

    override fun getAllAsMusicFolders(): Flow<List<MusicFolderEntity>> {
        return commonAppDB.musicFolderDao.getAllAsMusicFolders()
    }

    override suspend fun insertMusicFolder(musicFolder: MusicFolderEntity) {
        commonAppDB.musicFolderDao.insertMusicFolder(musicFolder)
    }

    override suspend fun insertAllMusicFolders(musicFolders: List<MusicFolderEntity>) {
        commonAppDB.musicFolderDao.insertAllMusicFolders(musicFolders)
    }

    override suspend fun deleteMusicFolder(id: Long) {
        commonAppDB.musicFolderDao.deleteMusicFolder(id)
    }

    override suspend fun getMusicFolderCount(): Int {
        return commonAppDB.musicFolderDao.getMusicFolderCount()
    }
}
