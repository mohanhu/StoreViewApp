package com.codeloop.storeviewapp.features.music.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.codeloop.storeviewapp.features.photo.domain.model.Folder

@Entity(tableName = MusicFolderTable.TABLE_NAME)
data class MusicFolderEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = MusicFolderTable.Columns.ID) var id: Long = 0,
    @ColumnInfo(name = MusicFolderTable.Columns.NAME) var name: String = "",
    @ColumnInfo(name = MusicFolderTable.Columns.PATH) var path: String = "",
    @ColumnInfo(name = MusicFolderTable.Columns.CREATED_AT) var createdAt: Long = 0,
    @ColumnInfo(name = MusicFolderTable.Columns.MUSIC_COUNT) var musicCount: Int = 0,
)


object MusicFolderTable {
    const val TABLE_NAME = "music_folders"
    object Columns {
        const val ID = "id"
        const val NAME = "name"
        const val PATH = "path"
        const val CREATED_AT = "created_at"
        const val MUSIC_COUNT = "music_count"
    }
}

fun Folder.toMusicFolderEntity() = MusicFolderEntity(
    id = id,
    name = name,
    path = relativePath,
    createdAt = date,
    musicCount = fileCount
)

fun MusicFolderEntity.toMusicFolder() = Folder(
    id = id,
    name = name,
    relativePath = path,
    date = createdAt,
    fileCount = musicCount
)
