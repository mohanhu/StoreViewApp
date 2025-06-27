package com.codeloop.storeviewapp.features.photo.data.local


import androidx.core.net.toUri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.codeloop.storeviewapp.features.photo.domain.model.MediaFile
import com.codeloop.storeviewapp.features.photo.domain.model.MediaFileType

@Entity(tableName = MediaFolderTable.TABLE_NAME)
data class MediaFolderEntity(

    @PrimaryKey(autoGenerate = false) @ColumnInfo(name = MediaFolderTable.Columns.ID) val id: Long,

    @ColumnInfo(name = MediaFolderTable.Columns.NAME) val name: String = "",

    @ColumnInfo(name = MediaFolderTable.Columns.URI) val uri: String = "",

    @ColumnInfo(name = MediaFolderTable.Columns.RELATIVE_PATH) val relativePath: String = "",

    @ColumnInfo(name = MediaFolderTable.Columns.DATE) val createdAt: Long,

    @ColumnInfo(name = MediaFolderTable.Columns.TYPE) val mediaFileType: MediaFileType = MediaFileType.Image
)

object MediaFolderTable {
    const val TABLE_NAME = "media_folders"

    object Columns {
        const val ID = "id"
        const val NAME = "name"
        const val URI = "URI"
        const val RELATIVE_PATH = "relativePath"
        const val DATE = "createdAt"
        const val TYPE = "media_file_type"
    }
}

fun MediaFolderEntity.toMediaFile(): MediaFile {
    return MediaFile(
        id = id,
        name = name,
        uri = uri.toUri(),
        createdAt = createdAt,
        mediaFileType = mediaFileType,
        relativePath = relativePath
    )
}

fun MediaFile.toMediaFileEntity(): MediaFolderEntity {
    return MediaFolderEntity(
        id = id,
        name = name,
        uri = uri.toString(),
        relativePath = relativePath,
        mediaFileType = mediaFileType,
        createdAt = createdAt
    )
}
