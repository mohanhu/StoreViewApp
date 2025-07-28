package com.codeloop.storeviewapp.features.photo.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.codeloop.storeviewapp.features.photo.domain.model.Folder

@Entity(tableName = PhotoFolderTable.TABLE_NAME)
data class PhotoFolderEntity (
    @PrimaryKey(autoGenerate = false) @ColumnInfo(name = PhotoFolderTable.Columns.ID) var id: Long = 0,
    @ColumnInfo(name = PhotoFolderTable.Columns.NAME) var name: String = "",
    @ColumnInfo(name = PhotoFolderTable.Columns.PATH) var path: String = "",
    @ColumnInfo(name = PhotoFolderTable.Columns.CREATED_AT) var createdAt: Long = 0,
    @ColumnInfo(name = PhotoFolderTable.Columns.PHOTO_COUNT) var photoCount: Int = 0,
)

object PhotoFolderTable {
    const val TABLE_NAME = "photo_folders"
    object Columns {
        const val ID = "id"
        const val NAME = "name"
        const val PATH = "path"
        const val CREATED_AT = "created_at"
        const val PHOTO_COUNT = "photo_count"
    }
}

fun Folder.toPhotoFolderEntity() = PhotoFolderEntity(
    id = id,
    name = name,
    path = relativePath,
    createdAt = date,
    photoCount = fileCount
)

fun PhotoFolderEntity.toPhotoFolder() = Folder(
    id = id,
    name = name,
    relativePath = path,
    date = createdAt,
    fileCount = photoCount
)

