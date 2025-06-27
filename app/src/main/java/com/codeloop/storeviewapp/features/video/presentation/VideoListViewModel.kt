package com.codeloop.storeviewapp.features.video.presentation

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
class VideoListViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val mediaRepository: MediaRepository,
    private val mediaFileLocalRepository: MediaFileLocalRepository,
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

        readFiles(folderName)
    }

    private fun readFiles(folderName: String) {
        mediaFileLocalRepository.getAllAsMediaFiles(MediaFileType.Video,folderName)
            .onEach { file ->
                val list : Map<String, List<MediaFolderEntity>> = file.groupBy {
                    ZoneTimer.formatByYearTimePattern(it.createdAt,"MMM dd yyyy")
                }
                val mediaFile = mutableListOf<VideoListUiModel>().apply {
                    list.forEach { key ->
                        val item = list[key.key]?:return@forEach
                        add(VideoListUiModel.Header(key.key.toString()))
                        add(VideoListUiModel.VideoList(item.map { it.toMediaFile() }))
                    }
                }
                _uiState.update {
                    it.copy(mediaFile = mediaFile)
                }
        }.launchIn(viewModelScope)
    }

    private fun onUiAction(photoUiAction: VideoListUiAction) {
        when(photoUiAction){
            is VideoListUiAction.FetchVideos -> fetchVideos(photoUiAction.folder)
        }
    }
    private fun fetchVideos(folderName:String) = viewModelScope.launch {
        mediaRepository.fetchVideos(folderName)
    }
}

sealed interface VideoListUiModel {
    data class Header(val message: String) : VideoListUiModel
    data class VideoList(val mediaFile: List<MediaFile>) : VideoListUiModel
}

data class VideoListUiState(
    val mediaFile: List<VideoListUiModel> = listOf(),
)

sealed interface VideoListUiAction {
    data class FetchVideos(val folder :String ): VideoListUiAction
}