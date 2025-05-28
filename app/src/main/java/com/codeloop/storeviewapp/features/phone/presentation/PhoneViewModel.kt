package com.codeloop.storeviewapp.features.phone.presentation

import android.content.Context
import android.provider.ContactsContract
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class PhoneViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(PhoneUiState())
    val uiState = _uiState.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), PhoneUiState()
    )

    val accept : (PhoneUiAction) -> Unit


    init {
        accept = ::onUiAction
    }

    private fun onUiAction(phoneUiAction: PhoneUiAction) {
        when (phoneUiAction) {
            PhoneUiAction.FetchPhoneList -> fetchPhoneList()
            PhoneUiAction.PermissionGranted -> {
                _uiState.update { it.copy(permissionGranted = true) }
                fetchPhoneList()
            }
        }
    }

    private fun fetchPhoneList() {

        val contactsList  = mutableListOf<PhoneList>()
        val contentResolver = context.contentResolver
        val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI

        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.PHOTO_URI,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
        )

        val cursor = contentResolver.query(uri, projection, null, null, null)

        cursor?.use {
            val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            val photoIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI)

            while (it.moveToNext()) {
                val name = it.getString(nameIndex)
                val number = it.getString(numberIndex)
                val image = it.getString(photoIndex)
                contactsList.add(
                    PhoneList(
                        id = number,
                        userName = name,
                        number = number,
                        userImage = image?:""
                    )
                )
            }
        }
        println("PhoneViewModel : >> $contactsList")
        _uiState.update { it.copy(phoneList = contactsList) }
    }
}

data class PhoneUiState(
    val phoneList : List<PhoneList> = listOf(),
    val permissionGranted : Boolean = false
)

sealed interface PhoneUiAction {
    data object FetchPhoneList : PhoneUiAction
    object PermissionGranted : PhoneUiAction
}

data class PhoneList (
    val id : String,
    val number : String,
    val userName : String,
    val userImage : String
)