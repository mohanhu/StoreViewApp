package com.codeloop.storeviewapp.features.music.presentation

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codeloop.storeviewapp.features.photo.data.local.toMediaFile
import com.codeloop.storeviewapp.features.photo.domain.model.MediaFile
import com.codeloop.storeviewapp.features.photo.domain.model.MediaFileType
import com.codeloop.storeviewapp.features.photo.domain.repository.MediaFileLocalRepository
import com.codeloop.storeviewapp.features.photo.domain.repository.MediaRepository
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
class MusicListViewmodel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val mediaRepository: MediaRepository,
    private val mediaFileLocalRepository: MediaFileLocalRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel () {

    private val _uiState = MutableStateFlow(MusicListUiState())
    val uiState = _uiState.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000),
        MusicListUiState()
    )

    val accept : (MusicListUiAction) -> Unit

    init {

        val folderName = savedStateHandle.get<String>("folderName")?:""
        fetchMusics(folderName)

        accept = ::onUiAction

        readFiles(folderName)
    }

    private fun readFiles(folderName: String) {
        mediaFileLocalRepository.getAllAsMediaFiles(MediaFileType.Music,folderName).onEach { file ->
            _uiState.update { it.copy(mediaFile = file.map { it.toMediaFile() }) }
        }.launchIn(viewModelScope)
    }

    private fun onUiAction(musicListUiAction: MusicListUiAction) {
        when(musicListUiAction){
            is MusicListUiAction.FetchMusic -> fetchMusics(musicListUiAction.folder)
        }
    }

    private fun fetchMusics(folderName:String) = viewModelScope.launch {
        mediaRepository.fetchAudio(folderName)
    }
}

data class MusicListUiState(
    val mediaFile: List<MediaFile> = listOf(),
)

sealed interface MusicListUiAction {
    data class FetchMusic(val folder :String ): MusicListUiAction
}