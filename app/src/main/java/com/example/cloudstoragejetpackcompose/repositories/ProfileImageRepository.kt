package com.example.cloudstoragejetpackcompose.repositories

import android.net.Uri
import com.example.cloudstoragejetpackcompose.other.Response
import kotlinx.coroutines.flow.Flow

interface ProfileImageRepository {
    suspend fun addImageToFirebaseStorage(imageUri: Uri): Flow<Response<Uri>>

    suspend fun addImageToCloudFirestore(downloadUrl: Uri): Flow<Response<Boolean>>

    suspend fun getImageFromFirestore(): Flow<Response<String>>
}