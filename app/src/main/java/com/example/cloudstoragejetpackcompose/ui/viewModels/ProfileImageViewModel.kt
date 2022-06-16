package com.example.cloudstoragejetpackcompose.ui.viewModels

import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cloudstoragejetpackcompose.other.Response
import com.example.cloudstoragejetpackcompose.other.Response.Success
import com.example.cloudstoragejetpackcompose.repositories.ProfileImageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileImageViewModel @Inject constructor(
    private val repo: ProfileImageRepository
) : ViewModel() {

    private val _addImageToStorageState = mutableStateOf<Response<Uri>>(Success(null))
    val addImageToStorageState: State<Response<Uri>> get() = _addImageToStorageState

    private val _addImageToDatabaseState = mutableStateOf<Response<Boolean>>(Success(null))
    val addImageToDatabaseState: State<Response<Boolean>> get() = _addImageToDatabaseState

    private val _getImageFromDatabaseState =
        mutableStateOf<Response<String>>(Success(null))
    val getImageFromDatabaseState: State<Response<String>> = _getImageFromDatabaseState


    fun addImageToStorage(imageUri: Uri) {
        viewModelScope.launch {
            repo.addImageToFirebaseStorage(imageUri).collect {
                _addImageToStorageState.value = it
            }
        }
    }

    fun addImageToDatabaseState(downloadUrl: Uri) {
        viewModelScope.launch {
            repo.addImageToFirestore(downloadUrl).collect {
                _addImageToDatabaseState.value = it
            }
        }
    }

    fun getImageFromDatabase() {
        viewModelScope.launch {
            repo.getImageFromFirestore().collect {
                _getImageFromDatabaseState.value = it
            }
        }
    }
}