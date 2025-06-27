package com.codeloop.storeviewapp.features.video.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codeloop.storeviewapp.features.photo.data.local.toMediaFile
import com.codeloop.storeviewapp.features.photo.domain.model.Folder
import com.codeloop.storeviewapp.features.photo.domain.model.MediaFileType
import com.codeloop.storeviewapp.features.photo.domain.repository.MediaFileLocalRepository
import com.codeloop.storeviewapp.features.photo.domain.repository.MediaRepository
import com.codeloop.storeviewapp.features.video.data.local.toFolder
import com.codeloop.storeviewapp.features.video.domain.repository.VideoLocalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class VideoViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val mediaRepository: MediaRepository,
    private val videoLocalRepository: VideoLocalRepository,
    private val mediaFileLocalRepository: MediaFileLocalRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(VideoUiState())
    val uiState = _uiState.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000),
        VideoUiState()
    )

    val accept: (VideoUiAction) -> Unit

    init {
        accept = ::onUiAction

        readFolders()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun readFolders() {
        viewModelScope.launch(Dispatchers.IO) {
            videoLocalRepository.getAllAsVideoFolders()
                .filter { it.isNotEmpty()  }
                .distinctUntilChanged()
                .flatMapLatest { folders ->
                    mediaFileLocalRepository.getAllAsMediaFiles(MediaFileType.Video, "")
                        .distinctUntilChanged()
                        .map { mediaList ->
                            val groupedMedia = mediaList.groupBy { it.relativePath }

                            val folders = folders.map { folder ->
                                val mediaFiles = groupedMedia[folder.path].orEmpty()
                                folder.toFolder().copy(
                                    mediaFile = mediaFiles.map { it.toMediaFile() }
                                )
                            }
                            folders
                        }
                }
                .collectLatest { mergedFolders ->
                    withContext(Dispatchers.Main) {
                        _uiState.update {
                            it.copy(mediaFileFolders = mergedFolders)
                        }
                    }
                }
        }
    }

    private fun onUiAction(videoUiAction: VideoUiAction) {
        when (videoUiAction) {
            VideoUiAction.PermissionGranted -> {
                _uiState.update {
                    it.copy(permissionGranted = true)
                }
                getVideoFolders()
            }
        }
    }

    private fun getVideoFolders() = viewModelScope.launch {
        mediaRepository.getVideoFolders()
    }
}

data class VideoUiState(
    val mediaFileFolders: List<Folder> = listOf(),
    val permissionGranted: Boolean = false
)

sealed interface VideoUiAction {
    data object PermissionGranted : VideoUiAction
}