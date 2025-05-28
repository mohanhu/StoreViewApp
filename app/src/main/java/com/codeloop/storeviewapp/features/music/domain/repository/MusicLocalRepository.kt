package com.codeloop.storeviewapp.features.music.domain.repository

import com.codeloop.storeviewapp.features.music.data.local.MusicFolderEntity
import kotlinx.coroutines.flow.Flow

interface MusicLocalRepository {

    fun getAllMusicFolders(): List<MusicFolderEntity>

    fun getAllAsMusicFolders(): Flow<List<MusicFolderEntity>>

    suspend fun insertMusicFolder(musicFolder: MusicFolderEntity)

    suspend fun insertAllMusicFolders(musicFolders: List<MusicFolderEntity>)

    suspend fun deleteMusicFolder(id: Long)

    suspend fun getMusicFolderCount(): Int
}
