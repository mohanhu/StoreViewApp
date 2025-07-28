package com.codeloop.storeviewapp.features.photo.data.local

import android.content.ContentUris
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import androidx.core.net.toUri
import androidx.room.Transaction
import com.codeloop.storeviewapp.features.music.data.local.toMusicFolderEntity
import com.codeloop.storeviewapp.features.music.domain.repository.MusicLocalRepository
import com.codeloop.storeviewapp.features.photo.domain.model.Folder
import com.codeloop.storeviewapp.features.photo.domain.model.MediaFile
import com.codeloop.storeviewapp.features.photo.domain.model.MediaFileType
import com.codeloop.storeviewapp.features.photo.domain.repository.MediaFileLocalRepository
import com.codeloop.storeviewapp.features.photo.domain.repository.MediaRepository
import com.codeloop.storeviewapp.features.photo.domain.repository.PhotoLocalRepository
import com.codeloop.storeviewapp.features.utils.zone.ZoneTimer
import com.codeloop.storeviewapp.features.video.data.local.toVideoFolderEntity
import com.codeloop.storeviewapp.features.video.domain.repository.VideoLocalRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

class MediaRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val photoLocalRepository: PhotoLocalRepository,
    private val musicLocalRepository : MusicLocalRepository,
    private val videoLocalRepository: VideoLocalRepository,
    private val mediaFileLocalRepository: MediaFileLocalRepository
) : MediaRepository {

    override fun getImageFolders() {
        CoroutineScope(Dispatchers.IO).launch {

            // API < 29	Deprecated, may still work
            val projection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                arrayOf(
                    MediaStore.Images.Media.RELATIVE_PATH,
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.DATE_MODIFIED
                )
            } else {
                arrayOf(
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.DATE_MODIFIED
                )
            }

            val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val cursor = context.contentResolver.query(uri, projection, null, null, null)

            val files = mutableListOf<Folder>()
            cursor?.use {

                while (cursor.moveToNext()) {

                    val columnId = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                    val dataColumn = cursor.getColumnIndexOrThrow(projection[0])
                    val columnDate = cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED)

                    val id = cursor.getLong(columnId)
                    val filePath = cursor.getString(dataColumn)
                    val date = cursor.getLong(columnDate) * 1000L

                    val file = File(filePath)
                    val folderName = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        filePath.trimEnd('/').substringAfterLast('/')
                    } else {
                        file.absolutePath
                    }
                    val relativePath = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        filePath
                    } else {
                        file.absolutePath
                    }
                    println("MediaRepositoryImpl : ${ZoneTimer.formatByYearTimePattern(date)}")

                    files.add(
                        Folder(
                            id = id,
                            name = folderName,
                            relativePath = relativePath,
                            fileCount = 0,
                            date = date
                        )
                    )
                }
            }
            val update: List<Folder> = files.groupBy { it.name }.map {
                fetchImages( it.value.first().relativePath)
                Folder(
                    id = it.value.first().id,
                    name = it.key,
                    fileCount = it.value.size,
                    relativePath = it.value.first().relativePath,
                    date = it.value.first().date
                )
            }.sortedBy {
                it.name
            }
            photoLocalRepository.insertAllPhotoFolder(update.map { it.toPhotoFolderEntity() })
        }
    }

    @Transaction
    override fun fetchImages(relativePath: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_MODIFIED
            )

            val sortOrder = "${MediaStore.Images.Media.DATE_MODIFIED} DESC"

            val selection = "${MediaStore.Images.Media.RELATIVE_PATH} = ?"
            val selectionArgs = arrayOf(relativePath)

            context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                val nameColumn = cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)
                val dateColumn = cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED)

                val file = mutableListOf<MediaFile>()
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val name = cursor.getString(nameColumn)
                    val date = cursor.getLong(dateColumn) * 1000L

                    val contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    val uri = ContentUris.withAppendedId(contentUri, id)
                    file.add(
                        MediaFile(
                            id,
                            name,
                            uri,
                            createdAt = date,
                            relativePath,
                            MediaFileType.Image
                        )
                    )
                }
                mediaFileLocalRepository.insertAllMediaFiles(file.map { it.toMediaFileEntity() })
                launch(Dispatchers.IO) {
                    val folders = mediaFileLocalRepository.getAllMediaFiles(MediaFileType.Image,"")
                    val deleteIds : MutableList<Long> = mutableListOf()
                    folders.map {
                        try {
                            context.contentResolver.openInputStream(it.uri.toUri())?.close()
                        }
                        catch (e: Exception){
                            deleteIds.add(it.id)
                        }
                    }
                    deleteIds.map {
                        mediaFileLocalRepository.deleteMediaFile(it)
                    }
                }
            }
        }
    }

    override fun getVideoFolders() {
        CoroutineScope(Dispatchers.IO).launch {

            // API < 29	Deprecated, may still work
            val projection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                arrayOf(
                    MediaStore.Video.Media.RELATIVE_PATH,
                    MediaStore.Video.Media._ID,
                )
            }
            else{
                arrayOf(
                    MediaStore.Video.Media.DATA,
                    MediaStore.Video.Media._ID
                )
            }

            val uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            val cursor = context.contentResolver.query(uri, projection, null, null, null)

            val files = mutableListOf<Folder>()
            cursor?.use {

                while (cursor.moveToNext()) {

                    val dataColumn = cursor.getColumnIndexOrThrow(projection[0])
                    val columnId = cursor.getColumnIndex(MediaStore.Video.Media._ID)

                    val filePath = cursor.getString(dataColumn)
                    val id = cursor.getLong(columnId)

                    val file = File(filePath)
                    val folderName = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                        filePath.trimEnd('/').substringAfterLast('/')
                    }
                    else{
                        file.absolutePath
                    }
                    val relativePath = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                        filePath
                    }
                    else{
                        file.absolutePath
                    }
                    files.add(Folder(id = id, name = folderName, relativePath = relativePath, fileCount = 0))
                }
            }
            val update : List<Folder> =  files.groupBy { it.name }.map {
                fetchVideos( it.value.first().relativePath)
                Folder(
                    id = it.value.first().id,
                    name = it.key,
                    fileCount = it.value.size,
                    relativePath = it.value.first().relativePath
                )
            }.sortedBy {
                it.name
            }
            videoLocalRepository.insertAllVideoFolder(update.map { it.toVideoFolderEntity() })
        }
    }

    @Transaction
    override fun fetchVideos(relativePath: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val projection = arrayOf(
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DATE_MODIFIED
            )

            val sortOrder = "${MediaStore.Video.Media.DATE_MODIFIED} DESC"

            val selection = "${MediaStore.Video.Media.RELATIVE_PATH} = ?"
            val selectionArgs = arrayOf(relativePath)

            context.contentResolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndex(MediaStore.Video.Media._ID)
                val nameColumn = cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME)
                val dateColumn = cursor.getColumnIndex(MediaStore.Video.Media.DATE_MODIFIED)

                val file = mutableListOf<MediaFile>()
                while (cursor.moveToNext()){
                    val id = cursor.getLong(idColumn)
                    val name = cursor.getString(nameColumn)
                    val date = cursor.getLong(dateColumn) * 1000L

                    val contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    val uri = ContentUris.withAppendedId(contentUri,id)
                    file.add(
                        MediaFile(
                            id = id,
                            name = name, relativePath = relativePath,
                            createdAt = date,
                            uri = uri, mediaFileType = MediaFileType.Video
                        ))
                }
                mediaFileLocalRepository.insertAllMediaFiles(file.map { it.toMediaFileEntity() })
                launch(Dispatchers.IO) {
                    val folders = mediaFileLocalRepository.getAllMediaFiles(MediaFileType.Video,"")
                    val deleteIds : MutableList<Long> = mutableListOf()
                    folders.map {
                        try {
                            context.contentResolver.openInputStream(it.uri.toUri())?.close()
                        }
                        catch (e: Exception){
                            deleteIds.add(it.id)
                        }
                    }
                    println("getAllAsMediaFiles isExist >> $deleteIds")
                    deleteIds.map {
                        mediaFileLocalRepository.deleteMediaFile(it)
                    }
                }
            }
        }
    }

    override fun getAudioFolders() {
        CoroutineScope(Dispatchers.IO).launch {

            // API < 29	Deprecated, may still work
            val projection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                arrayOf(
                    MediaStore.Audio.Media.RELATIVE_PATH,
                    MediaStore.Audio.Media._ID,
                )
            }
            else{
                arrayOf(
                    MediaStore.Audio.Media.DATA,
                    MediaStore.Audio.Media._ID
                )
            }

            val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            val cursor = context.contentResolver.query(uri, projection, null, null, null)

            val files = mutableListOf<Folder>()
            cursor?.use {
                val dataColumn = cursor.getColumnIndexOrThrow(projection[0])

                while (cursor.moveToNext()) {

                    val columnId = cursor.getColumnIndex(MediaStore.Audio.Media._ID)

                    val filePath = cursor.getString(dataColumn)

                    val id = cursor.getLong(columnId)

                    val file = File(filePath)
                    val folderName = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                        filePath.trimEnd('/').substringAfterLast('/')
                    }
                    else{
                        file.absolutePath
                    }
                    val relativePath = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                        filePath
                    }
                    else{
                        file.absolutePath
                    }
                    files.add(Folder(id = id, name = folderName, relativePath = relativePath, fileCount = 0))
                }
            }
            val update : List<Folder> =  files.groupBy { it.name }.map {
                fetchAudio( it.value.first().relativePath)
                Folder(
                    id = it.value.first().id,
                    name = it.key,
                    fileCount = it.value.size,
                    relativePath = it.value.first().relativePath
                )
            }.sortedBy {
                it.name
            }
            println("PhotoViewModel : $update")
            musicLocalRepository.insertAllMusicFolders(update.map { it.toMusicFolderEntity() })
        }
    }

    override fun fetchAudio(relativePath: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val projection = arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DATE_MODIFIED
            )

            val sortOrder = "${MediaStore.Audio.Media.DATE_MODIFIED} DESC"

            val selection = "${MediaStore.Audio.Media.RELATIVE_PATH} = ?"
            val selectionArgs = arrayOf(relativePath)

            context.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
                val nameColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)
                val dateColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED)

                val file = mutableListOf<MediaFile>()
                while (cursor.moveToNext()){
                    val id = cursor.getLong(idColumn)
                    val name = cursor.getString(nameColumn)
                    val date = cursor.getLong(dateColumn) * 1000L

                    val contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    val uri = ContentUris.withAppendedId(contentUri,id)
                    file.add(MediaFile(
                        id,
                        name,
                        uri,
                        createdAt = date,
                        relativePath = relativePath,
                        MediaFileType.Music
                    ))
                }
                mediaFileLocalRepository.insertAllMediaFiles(file.map { it.toMediaFileEntity() })
                launch(Dispatchers.IO) {
                    val folders = mediaFileLocalRepository.getAllMediaFiles(MediaFileType.Music,"")
                    val deleteIds : MutableList<Long> = mutableListOf()
                    folders.map {
                        try {
                            context.contentResolver.openInputStream(it.uri.toUri())?.close()
                        }
                        catch (e: Exception){
                            deleteIds.add(it.id)
                        }
                    }
                    deleteIds.forEach {
                        mediaFileLocalRepository.deleteMediaFile(it)
                    }
                }
            }
        }
    }
}
























































