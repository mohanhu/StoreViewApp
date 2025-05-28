package com.codeloop.storeviewapp.features.video.presentation

import android.content.Context
import android.os.Build
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codeloop.storeviewapp.features.music.data.local.toMusicFolder
import com.codeloop.storeviewapp.features.photo.domain.model.Folder
import com.codeloop.storeviewapp.features.photo.domain.model.MediaFile
import com.codeloop.storeviewapp.features.video.data.local.toFolder
import com.codeloop.storeviewapp.features.video.data.local.toVideoFolderEntity
import com.codeloop.storeviewapp.features.video.domain.repository.VideoLocalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import kotlin.collections.map
import kotlin.collections.toList

@HiltViewModel
class VideoViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val videoLocalRepository: VideoLocalRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VideoUiState())
    val uiState = _uiState.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000),
        VideoUiState()
    )

    val accept: (VideoUiAction) -> Unit

    init {
        accept = ::onUiAction

        _uiState.asStateFlow().map { it.currentSelectFolder }.distinctUntilChanged()
            .onEach { folder ->

            }
            .launchIn(viewModelScope)

        readFolders()
    }

    private fun readFolders() {
        videoLocalRepository.getAllAsVideoFolders().onEach { folder ->
            if (_uiState.value.permissionGranted){
                _uiState.update {
                    it.copy(mediaFileFolders = folder.map {
                        it.toFolder()
                    }.toList(),
                        currentSelectFolder = folder.firstOrNull()?.name?:""
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun onUiAction(videoUiAction: VideoUiAction) {
        when (videoUiAction) {
            VideoUiAction.FetchVideos -> getVideoFolders(context)
            VideoUiAction.PermissionGranted -> {
                _uiState.update {
                    it.copy(permissionGranted = true)
                }
                getVideoFolders(context)
            }
        }
    }

    private fun getVideoFolders(context: Context) = viewModelScope.launch {

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

data class VideoUiState(
    val mediaFileFolders: List<Folder> = listOf(),
    val mediaFile: List<MediaFile> = listOf(),
    val currentSelectFolder: String = "",
    val permissionGranted: Boolean = false
)

sealed interface VideoUiAction {
    data object FetchVideos : VideoUiAction
    data object PermissionGranted : VideoUiAction
}