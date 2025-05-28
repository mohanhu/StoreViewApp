package com.codeloop.storeviewapp.features.music.presentation

import android.content.Context
import android.os.Build
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codeloop.storeviewapp.features.music.data.local.toMusicFolder
import com.codeloop.storeviewapp.features.music.data.local.toMusicFolderEntity
import com.codeloop.storeviewapp.features.music.domain.repository.MusicLocalRepository
import com.codeloop.storeviewapp.features.photo.data.local.toPhotoFolder
import com.codeloop.storeviewapp.features.photo.domain.model.Folder
import com.codeloop.storeviewapp.features.photo.domain.model.MediaFile
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
class MusicViewModel
 @Inject constructor(
     @ApplicationContext private val context: Context,
     private val musicLocalRepository: MusicLocalRepository
 ) : ViewModel() {

     private val _uiState = MutableStateFlow(MusicUiState())
    val uiState = _uiState.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000),
        MusicUiState()
    )

    val accept : (MusicUiAction) -> Unit

    init {
        accept = ::onUiAction

        _uiState.asStateFlow().map { it.currentSelectFolder }.distinctUntilChanged().onEach { folder ->
            if (folder.isNotBlank()){
            }
        }.launchIn(viewModelScope)

        readFolderList()
    }

    private fun readFolderList() {
        musicLocalRepository.getAllAsMusicFolders().onEach { folder ->
            if (_uiState.value.permissionGranted){
                _uiState.update {
                    it.copy(mediaFileFolders = folder.map {
                        it.toMusicFolder()
                    }.toList(),
                        currentSelectFolder = folder.firstOrNull()?.name?:""
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun onUiAction(musicUiAction: MusicUiAction) {
        when(musicUiAction){
            MusicUiAction.FetchMusic -> getMusicFolders(context)
            MusicUiAction.PermissionGranted -> {
                _uiState.update { it.copy(permissionGranted = true) }
                getMusicFolders(context)
            }
        }
    }

    private fun getMusicFolders(context: Context) = viewModelScope.launch {

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

data class MusicUiState (
    val mediaFileFolders: List<Folder> = listOf(),
    val mediaFile: List<MediaFile> = listOf(),
    val currentSelectFolder : String = "",
    val permissionGranted : Boolean = false
)

sealed interface MusicUiAction {
    data object FetchMusic: MusicUiAction
    data object PermissionGranted : MusicUiAction
}
