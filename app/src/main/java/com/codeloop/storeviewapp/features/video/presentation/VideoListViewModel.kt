package com.codeloop.storeviewapp.features.video.presentation

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codeloop.storeviewapp.features.photo.data.local.toMediaFile
import com.codeloop.storeviewapp.features.photo.data.local.toMediaFileEntity
import com.codeloop.storeviewapp.features.photo.domain.model.MediaFile
import com.codeloop.storeviewapp.features.photo.domain.model.MediaFileType
import com.codeloop.storeviewapp.features.photo.domain.repository.MediaFileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoListViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val mediaFileRepository: MediaFileRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel () {

    private val _uiState = MutableStateFlow(VideoListUiState())
    val uiState = _uiState.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000),
        VideoListUiState()
    )

    val accept : (VideoListUiAction) -> Unit

    init {
        val folderName = savedStateHandle.get<String>("folderName")?:""

        fetchVideos(folderName)

        accept = ::onUiAction

        readFiles()
    }

    private fun readFiles() {
        mediaFileRepository.getAllAsMediaFiles(MediaFileType.Video).onEach { file ->
            _uiState.update { it.copy(mediaFile = file.map { it.toMediaFile() }) }
        }.launchIn(viewModelScope)
    }

    private fun onUiAction(photoUiAction: VideoListUiAction) {
        when(photoUiAction){
            is VideoListUiAction.FetchVideos -> fetchVideos(photoUiAction.folder)
        }
    }
    private fun fetchVideos(folderName:String) = viewModelScope.launch {
        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.RELATIVE_PATH
        )

        val sortOrder = "${MediaStore.Video.Media.DATE_TAKEN} DESC"

        val selection = "${MediaStore.Video.Media.RELATIVE_PATH} = ?"
        val selectionArgs = arrayOf("$folderName")

        context.contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndex(MediaStore.Video.Media._ID)
            val nameColumn = cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME)

            val file = mutableListOf<MediaFile>()
            while (cursor.moveToNext()){
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)

                val contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                val uri = ContentUris.withAppendedId(contentUri,id)
                file.add(
                    MediaFile(
                    id = id,
                    name = name,
                    uri = uri, mediaFileType = MediaFileType.Video
                ))
            }
            mediaFileRepository.insertAllMediaFiles(file.map { it.toMediaFileEntity() })
        }
    }
}

data class VideoListUiState(
    val mediaFile: List<MediaFile> = listOf(),
)

sealed interface VideoListUiAction {
    data class FetchVideos(val folder :String ): VideoListUiAction
}