package com.codeloop.storeviewapp.features.photo.presentation

import android.content.Context
import android.os.Build
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codeloop.storeviewapp.features.photo.data.local.toPhotoFolder
import com.codeloop.storeviewapp.features.photo.data.local.toPhotoFolderEntity
import com.codeloop.storeviewapp.features.photo.domain.model.Folder
import com.codeloop.storeviewapp.features.photo.domain.model.MediaFile
import com.codeloop.storeviewapp.features.photo.domain.repository.PhotoLocalRepository
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
import kotlin.collections.first
import kotlin.collections.toList

@HiltViewModel
class PhotoViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val photoLocalRepository: PhotoLocalRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(PhotoUiState())
    val uiState = _uiState.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000),
        PhotoUiState()
    )
    
    val accept : (PhotoUiAction) -> Unit
    
    init {
        accept = ::onUiAction

        _uiState.asStateFlow().map { it.currentSelectFolder }.distinctUntilChanged().onEach { folder ->
            if (folder.isNotBlank()){
            }
        }.launchIn(viewModelScope)

        readFolderList()
    }

    private fun readFolderList() {
        photoLocalRepository.getAllAsPhotoFolders().onEach { folder ->
           if (_uiState.value.permissionGranted){
               _uiState.update {
                   it.copy(mediaFileFolders = folder.map {
                       it.toPhotoFolder()
                   }.toList(), currentSelectFolder = folder.firstOrNull()?.name?:""
                   )
               }
           }
        }.launchIn(viewModelScope)
    }

    private fun onUiAction(photoUiAction: PhotoUiAction) {
        when(photoUiAction){
            PhotoUiAction.FetchImages -> getImageFolders(context)
            PhotoUiAction.PermissionGranted -> {
                _uiState.update { it.copy(permissionGranted = true) }
                getImageFolders(context)
            }
        }
    }

    private fun getImageFolders(context: Context) = viewModelScope.launch {

        // API < 29	Deprecated, may still work
        val projection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            arrayOf(
                MediaStore.Images.Media.RELATIVE_PATH,
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATE_MODIFIED
            )
        }
        else{
            arrayOf(
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATE_MODIFIED
            )
        }

        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val cursor = context.contentResolver.query(uri, projection, null, null, null)

        val files = mutableListOf<Folder>()
        cursor?.use {

            while (cursor.moveToNext()) {

                val columnId = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                val dataColumn = cursor.getColumnIndexOrThrow(projection[0])
                val columnDate = cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED)

                val id = cursor.getLong(columnId)
                val filePath = cursor.getString(dataColumn)
                val date = cursor.getLong(columnDate)

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
                files.add(Folder(id = id, name = folderName, relativePath = relativePath, fileCount = 0, date = date))
            }
        }
        val update : List<Folder> =  files.groupBy { it.name }.map {
            Folder(
                id = it.value.first().id,
                name = it.key,
                fileCount = it.value.size,
                relativePath = it.value.first().relativePath,
                date = it.value.first().date
            )
        }.sortedBy {
            it.name
        }
        println("PhotoViewModel : $update")
        photoLocalRepository.insertAllPhotoFolder(update.map { it.toPhotoFolderEntity() })
    }
}

data class PhotoUiState (
    val mediaFileFolders: List<Folder> = listOf(),
    val mediaFile: List<MediaFile> = listOf(),
    val currentSelectFolder : String = "",
    val permissionGranted : Boolean = false
)

sealed interface PhotoUiAction {
    data object FetchImages : PhotoUiAction
    data object PermissionGranted : PhotoUiAction
}