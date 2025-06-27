package com.codeloop.storeviewapp.features.photo.presentation

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codeloop.storeviewapp.features.photo.data.local.MediaFolderEntity
import com.codeloop.storeviewapp.features.photo.data.local.toMediaFile
import com.codeloop.storeviewapp.features.photo.domain.model.MediaFile
import com.codeloop.storeviewapp.features.photo.domain.model.MediaFileType
import com.codeloop.storeviewapp.features.photo.domain.repository.MediaFileLocalRepository
import com.codeloop.storeviewapp.features.photo.domain.repository.MediaRepository
import com.codeloop.storeviewapp.features.utils.zone.ZoneTimer
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
    private val mediaRepository: MediaRepository,
    private val mediaFileLocalRepository: MediaFileLocalRepository,
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

        readFiles(folderName)
    }

    private fun readFiles(folderName: String) {
        mediaFileLocalRepository.getAllAsMediaFiles(MediaFileType.Image,folderName).onEach { file ->
            val list : Map<String, List<MediaFolderEntity>> = file.groupBy {
                ZoneTimer.formatByYearTimePattern(it.createdAt,"MMM dd yyyy")
            }
            val mediaFile = mutableListOf<PhotoListUiModel>().apply {
                list.forEach { key ->
                    val item = list[key.key]?:return@forEach
                    add(PhotoListUiModel.Header(key.key.toString()))
                    add(PhotoListUiModel.ImageList(item.map { it.toMediaFile() }))
                }
            }
            _uiState.update {
                it.copy(mediaFile = mediaFile)
            }
        }.launchIn(viewModelScope)
    }

    private fun onUiAction(photoUiAction: PhotoListUiAction) {
        when(photoUiAction){
            is PhotoListUiAction.FetchImages -> fetchImages(photoUiAction.folder)
            else -> {}
        }
    }
    private fun fetchImages(relativePath:String) = viewModelScope.launch {
       mediaRepository.fetchImages(relativePath)
    }
}

sealed interface PhotoListUiModel {
    data class Header(val message: String) : PhotoListUiModel
    data class ImageList(val mediaFile: List<MediaFile>) : PhotoListUiModel
}

data class PhotoListUiState(
    val mediaFile: List<PhotoListUiModel> = listOf(),
)

sealed interface PhotoListUiAction {
    data class FetchImages(val folder :String ): PhotoListUiAction
}