package com.codeloop.storeviewapp.features.photo.domain.model

import android.net.Uri

data class MediaFile(
    val id:Long,
    val name:String,
    val uri: Uri,
    val createdAt: Long,
    val relativePath: String,
    val mediaFileType : MediaFileType
)

enum class MediaFileType {
    Image,
    Video,
    Music
}

data class Folder (
    val id :Long,
    val name :String,
    val fileCount:Int,
    val relativePath: String,
    val date : Long = 0L,
    val mediaFile: List<MediaFile> = listOf(),
)