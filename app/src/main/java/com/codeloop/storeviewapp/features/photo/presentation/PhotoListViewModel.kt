package com.codeloop.storeviewapp.features.photo.presentation

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
class PhotoListViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val mediaFileRepository: MediaFileRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel () {

    private val _uiState = MutableStateFlow(PhotoListUiState())
    val uiState = _uiState.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000),
        PhotoListUiState()
    )

    val accept : (PhotoListUiAction) -> Unit

    init {

        val folderName = savedStateHandle.get<String>("folderName")?:""
        fetchImages(folderName)

        accept = ::onUiAction

        readFiles()
    }

    private fun readFiles() {
        mediaFileRepository.getAllAsMediaFiles(MediaFileType.Image).onEach { file ->
            _uiState.update { it.copy(mediaFile = file.map { it.toMediaFile() }) }
        }.launchIn(viewModelScope)
    }

    private fun onUiAction(photoUiAction: PhotoListUiAction) {
        when(photoUiAction){
            is PhotoListUiAction.FetchImages -> fetchImages(photoUiAction.folder)
        }
    }
    private fun fetchImages(folderName:String) = viewModelScope.launch {
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
        )

        val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC"

        val selection = "${MediaStore.Images.Media.RELATIVE_PATH} = ?"
        val selectionArgs = arrayOf("$folderName")

        context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndex(MediaStore.Images.Media._ID)
            val nameColumn = cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)

            val file = mutableListOf<MediaFile>()
            while (cursor.moveToNext()){
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)

                val contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                val uri = ContentUris.withAppendedId(contentUri,id)
                file.add(MediaFile(
                    id,
                    name,
                    uri,
                    MediaFileType.Image
                ))
            }
            mediaFileRepository.insertAllMediaFiles(file.map { it.toMediaFileEntity() })
        }
    }
}


data class PhotoListUiState(
    val mediaFile: List<MediaFile> = listOf(),
    val permissionGranted: Boolean = false
)

sealed interface PhotoListUiAction {
    data class FetchImages(val folder :String ): PhotoListUiAction
}