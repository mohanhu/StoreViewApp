package com.codeloop.storeviewapp.features.photo.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codeloop.storeviewapp.features.photo.data.local.toMediaFile
import com.codeloop.storeviewapp.features.photo.data.local.toPhotoFolder
import com.codeloop.storeviewapp.features.photo.domain.model.Folder
import com.codeloop.storeviewapp.features.photo.domain.model.MediaFileType
import com.codeloop.storeviewapp.features.photo.domain.repository.MediaFileLocalRepository
import com.codeloop.storeviewapp.features.photo.domain.repository.MediaRepository
import com.codeloop.storeviewapp.features.photo.domain.repository.PhotoLocalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PhotoViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val mediaRepository: MediaRepository,
    private val photoLocalRepository: PhotoLocalRepository,
    private val mediaFileLocalRepository: MediaFileLocalRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(PhotoUiState())
    val uiState = _uiState.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000),
        PhotoUiState()
    )
    
    val accept : (PhotoUiAction) -> Unit
    
    init {
        accept = ::onUiAction

        readFolderList()

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun readFolderList() {
        viewModelScope.launch(Dispatchers.IO) {
            photoLocalRepository.getAllAsPhotoFolders()
                .filter { it.isNotEmpty()  }
                .flatMapLatest { folders ->
                    mediaFileLocalRepository.getAllAsMediaFiles(MediaFileType.Image, "")
                        .map { mediaList ->
                            val groupedMedia = mediaList.groupBy { it.relativePath }

                            val folders = folders.map { folder ->
                                val mediaFiles = groupedMedia[folder.path].orEmpty()
                                folder.toPhotoFolder().copy(
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

    private fun onUiAction(photoUiAction: PhotoUiAction) {
        when(photoUiAction){
            PhotoUiAction.PermissionGranted -> {
                _uiState.update { it.copy(permissionGranted = true) }
                getImageFolders()
            }
        }
    }

    private fun getImageFolders() = viewModelScope.launch {
        mediaRepository.getImageFolders()
    }
}

data class PhotoUiState (
    val mediaFileFolders: List<Folder> = listOf(),
    val permissionGranted : Boolean = false
)

sealed interface PhotoUiAction {
    data object PermissionGranted : PhotoUiAction
}