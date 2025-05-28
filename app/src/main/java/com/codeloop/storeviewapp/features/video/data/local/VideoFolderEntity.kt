package com.codeloop.storeviewapp.features.video.data.local


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.codeloop.storeviewapp.features.photo.domain.model.Folder

@Entity(tableName = VideoFolderTable.TABLE_NAME)
data class VideoFolderEntity (
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = VideoFolderTable.Columns.ID) var id: Long = 0,
    @ColumnInfo(name = VideoFolderTable.Columns.NAME) var name: String = "",
    @ColumnInfo(name = VideoFolderTable.Columns.PATH) var path: String = "",
    @ColumnInfo(name = VideoFolderTable.Columns.CREATED_AT) var createdAt: Long = 0,
    @ColumnInfo(name = VideoFolderTable.Columns.VIDEO_COUNT) var videoCount: Int = 0,
)

object VideoFolderTable {
    const val TABLE_NAME = "video_folders"
    object Columns {
        const val ID = "id"
        const val NAME = "name"
        const val PATH = "path"
        const val CREATED_AT = "created_at"
        const val VIDEO_COUNT = "video_count"
    }
}

fun Folder.toVideoFolderEntity() = VideoFolderEntity(
    id = id,
    name = name,
    path = relativePath,
    createdAt = date,
    videoCount = fileCount
)

fun VideoFolderEntity.toFolder() = Folder(
    id = id,
    name = name,
    relativePath = path,
    date = createdAt,
    fileCount = videoCount
)
