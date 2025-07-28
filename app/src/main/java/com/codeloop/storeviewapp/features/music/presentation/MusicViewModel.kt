package com.codeloop.storeviewapp.features.music.presentation

import android.content.Context
import android.os.Build
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codeloop.storeviewapp.features.music.data.local.toMusicFolder
import com.codeloop.storeviewapp.features.music.data.local.toMusicFolderEntity
import com.codeloop.storeviewapp.features.music.domain.repository.MusicLocalRepository
import com.codeloop.storeviewapp.features.photo.data.local.toMediaFile
import com.codeloop.storeviewapp.features.photo.data.local.toPhotoFolder
import com.codeloop.storeviewapp.features.photo.domain.model.Folder
import com.codeloop.storeviewapp.features.photo.domain.model.MediaFile
import com.codeloop.storeviewapp.features.photo.domain.model.MediaFileType
import com.codeloop.storeviewapp.features.photo.domain.repository.MediaFileLocalRepository
import com.codeloop.storeviewapp.features.photo.domain.repository.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
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

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MusicViewModel
 @Inject constructor(
     @ApplicationContext private val context: Context,
     private val mediaRepository: MediaRepository,
     private val musicLocalRepository: MusicLocalRepository,
     private val mediaFileLocalRepository: MediaFileLocalRepository,
 ) : ViewModel() {

     private val _uiState = MutableStateFlow(MusicUiState())
    val uiState = _uiState.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000),
        MusicUiState()
    )

    val accept : (MusicUiAction) -> Unit

    init {
        accept = ::onUiAction

        _uiState.asStateFlow().map { it.currentSelectFolder }.distinctUntilChanged()
            .flatMapLatest { folder ->
                println("getAllAsMediaFiles`` folder : $folder")
                mediaFileLocalRepository.getAllAsMediaFiles(mediaFileType = MediaFileType.Music, folder)
        }.distinctUntilChanged()
            .onEach { mediaFiles ->
                _uiState.update {
                    it.copy(mediaFile = mediaFiles.map { it.toMediaFile() })
                }
            }
            .launchIn(viewModelScope)

        readFolderList()
    }

    private fun readFolderList() {
        musicLocalRepository.getAllAsMusicFolders().onEach { folder ->
            if (_uiState.value.permissionGranted){
                _uiState.update {
                    it.copy(mediaFileFolders = folder.map {
                        it.toMusicFolder()
                    }.toList())
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun onUiAction(musicUiAction: MusicUiAction) {
        when(musicUiAction){
            MusicUiAction.PermissionGranted -> {
                _uiState.update { it.copy(permissionGranted = true) }
                getMusicFolders(context)
            }

            is MusicUiAction.UpdateFolderName -> {
                _uiState.update { it.copy(currentSelectFolder = musicUiAction.folderName) }
            }
        }
    }

    private fun getMusicFolders(context: Context) = viewModelScope.launch {
        mediaRepository.getAudioFolders()
    }
 }

data class MusicUiState (
    val mediaFileFolders: List<Folder> = listOf(),
    val mediaFile: List<MediaFile> = listOf(),
    val currentSelectFolder : String = "",
    val permissionGranted : Boolean = false
)

sealed interface MusicUiAction {

    data class UpdateFolderName (val folderName : String) : MusicUiAction
    data object PermissionGranted : MusicUiAction
}
