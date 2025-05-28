package com.codeloop.storeviewapp.features.docs.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import javax.inject.Inject

@HiltViewModel
class DocumentViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(DocumentUiState())
    val uiState = _uiState.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000),
        DocumentUiState()
    )

    val accept : (DocumentUiAction) -> Unit

    init {
        accept = ::onUiAction

        _uiState.asStateFlow().map { it.currentSelectFolder }.distinctUntilChanged().onEach { folder ->
            if (folder.isNotBlank()){
//                fetchDocuments(folder)
            }
        }.launchIn(viewModelScope)
    }

    private fun onUiAction(documentUiAction: DocumentUiAction) {
        when(documentUiAction){
            DocumentUiAction.FetchDocs -> getAllFiles(context)
        }
    }

    fun getAllFiles(context: Context) {

    }
}

data class DocumentUiState (
    val mediaFileFolders: List<Folder> = listOf(),
    val mediaFile: List<MediaFile> = listOf(),
    val currentSelectFolder : String = ""
)

sealed interface DocumentUiAction {
    data object FetchDocs : DocumentUiAction
}