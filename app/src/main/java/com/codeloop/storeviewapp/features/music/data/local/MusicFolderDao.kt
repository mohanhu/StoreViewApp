package com.codeloop.storeviewapp.features.music.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MusicFolderDao {

    @Query("SELECT * FROM ${MusicFolderTable.TABLE_NAME} ORDER BY ${MusicFolderTable.Columns.CREATED_AT} DESC")
    fun getAllMusicFolders(): List<MusicFolderEntity>

    @Query("SELECT * FROM ${MusicFolderTable.TABLE_NAME} ORDER BY ${MusicFolderTable.Columns.CREATED_AT} DESC")
    fun getAllAsMusicFolders(): Flow<List<MusicFolderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMusicFolder(musicFolder: MusicFolderEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllMusicFolders(musicFolders: List<MusicFolderEntity>)

    @Query("DELETE FROM ${MusicFolderTable.TABLE_NAME} WHERE ${MusicFolderTable.Columns.ID} = :id")
    suspend fun deleteMusicFolder(id: Long)

    @Query("SELECT COUNT(*) FROM ${MusicFolderTable.TABLE_NAME}")
    suspend fun getMusicFolderCount(): Int
}
